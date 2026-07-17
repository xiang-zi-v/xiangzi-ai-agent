package com.xiangzi.xiangziaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.xiangzi.xiangziaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用工具
    private final ToolCallback[] availableTools;

    // 保存用于大模型的响应
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用内容的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withInternalToolExecutionEnabled(false)
                .build();
    }

    @Override
    public boolean think(SseEmitter emitter) {
        if (StrUtil.isNotBlank(this.getNextStepPrompt())) {
            // 大模型是没有记忆的，每一次思考都要重新确定思考的提示词和历史上下文
            UserMessage userMessage = new UserMessage(this.getNextStepPrompt());
            this.getMessagesList().add(userMessage);
        }
        try {
            List<Message> messageList = this.getMessagesList();
            Prompt prompt = new Prompt(messageList, this.chatOptions);

            // 流式获取大模型思考结果，像打字机一样逐块下发
            Flux<ChatResponse> chatResponseFlux = this.getChatClient()
                    .prompt(prompt)
                    .system(this.getSysPrompt())
                    .tools(this.availableTools)
                    .stream()
                    .chatResponse();

            // 用于聚合所有流式 chunk 的 text 和 toolCalls，最终合成一条完整的 AssistantMessage
            StringBuilder textBuffer = new StringBuilder();
            List<AssistantMessage.ToolCall> accumulatedToolCalls = new ArrayList<>();
            AtomicReference<ChatResponse> lastChatResponseRef = new AtomicReference<>();

            // 支持用户手动取消：当 Agent 状态变为 CANCELLED 时中断 Flux 流
            chatResponseFlux = chatResponseFlux
                    .takeWhile(chunk -> this.getState() != AgentState.CANCELLED);

            // 消费 Flux：doOnNext 逐块处理（打字机下发 + 聚合文本 + 收集 toolCalls），
            // 然后通过 blockLast() 等待流式全部返回并得到最后一个 ChatResponse。
            ChatResponse lastChatResponse = chatResponseFlux
                    .doOnNext(chunk -> {
                        if (chunk == null) {
                            return;
                        }
                        lastChatResponseRef.compareAndSet(null, chunk);
                        Generation generation = chunk.getResult();
                        if (generation != null && generation.getOutput() != null) {
                            AssistantMessage output = generation.getOutput();
                            // 累加文字内容，并立即下发到前端（打字机效果）
                            String deltaText = output.getText();
                            if (StrUtil.isNotBlank(deltaText)) {
                                textBuffer.append(deltaText);
                                if (emitter != null) {
                                    try {
                                        emitter.send(deltaText);
                                    } catch (IOException e) {
                                        emitter.completeWithError(e);
                                    }
                                }
                            }
                            // 累加工具调用（流式场景中 toolCalls 通常在末尾 chunk 出现，也可能是增量形式）
                            List<AssistantMessage.ToolCall> toolCalls = output.getToolCalls();
                            if (toolCalls != null && !toolCalls.isEmpty()) {
                                accumulatedToolCalls.addAll(toolCalls);
                            }
                            // 持续更新最新的 ChatResponse 引用
                            lastChatResponseRef.set(chunk);
                        }
                    })
                    .doOnError(error -> {
                        if (this.getState() == AgentState.CANCELLED) {
                            // 用户主动取消导致的错误，忽略
                            return;
                        }
                        log.error(getName() + "的流式思考过程遇到了问题: " + error.getMessage(), error);
                        if (emitter != null) {
                            try {
                                emitter.send("处理时遇到错误: " + error.getMessage());
                            } catch (IOException ignored) {
                            }
                            emitter.completeWithError(error);
                        }
                    })
                    .blockLast();

            // 兜底：如果 blockLast() 没有返回（例如流为空），使用 doOnNext 记录的最后一条
            if (lastChatResponse == null) {
                lastChatResponse = lastChatResponseRef.get();
            }

            // 合成最终的 ChatResponse（text 为累加后的完整文本）
            ChatResponse finalChatResponse;
            if (lastChatResponse != null) {
                AssistantMessage originalAssistant = lastChatResponse.getResult().getOutput();
                List<AssistantMessage.ToolCall> finalToolCalls = CollUtil.isNotEmpty(originalAssistant.getToolCalls())
                        ? originalAssistant.getToolCalls()
                        : accumulatedToolCalls;
                AssistantMessage mergedAssistant = new AssistantMessage(textBuffer.toString(), originalAssistant.getMetadata(), finalToolCalls);
                Generation mergedGeneration = new Generation(mergedAssistant);
                List<Generation> mergedResults = new ArrayList<>();
                mergedResults.add(mergedGeneration);
                finalChatResponse = new ChatResponse(mergedResults, lastChatResponse.getMetadata());
            } else {
                AssistantMessage emptyAssistant = new AssistantMessage(textBuffer.toString());
                Generation emptyGeneration = new Generation(emptyAssistant);
                List<Generation> emptyResults = new ArrayList<>();
                emptyResults.add(emptyGeneration);
                finalChatResponse = new ChatResponse(emptyResults);
            }

            // 保存大模型的响应
            this.toolCallChatResponse = finalChatResponse;

            // 大模型的思考结果
            AssistantMessage assistantMessage = finalChatResponse.getResult().getOutput();
            // 大模型调用的工具列表
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();

            // 输出日志
            this.outlog(assistantMessage, toolCallList);

            if (toolCallList == null || toolCallList.isEmpty()) {
                // 没有调用工具，将大模型的思考结果添加到 memory 列表中
                messageList.add(assistantMessage);
                return false;
            } else {
                // 需要调用工具时，无需记录大模型的思考结果，因为调用工具时会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题: " + e.getMessage());
            this.getMessagesList().add(
                    new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        if (!this.toolCallChatResponse.hasToolCalls()) {
            return "没有工具可以调用";
        }

        // 调用工具并获取工具调用结果
        Prompt prompt = new Prompt(this.getMessagesList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = this.toolCallingManager.executeToolCalls(prompt, this.toolCallChatResponse);

        // conversationHistory 已经包含了历史上下文和工具调用返回的结果
        List<Message> messageList = toolExecutionResult.conversationHistory();
        this.setMessagesList(messageList);

        // 获取最后一条工具调用结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());

        String result = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + "完成了任务，结果是：" + response.responseData()).collect(Collectors.joining("\n"));

        // 检查ai是否调用了终止工具
        boolean isTerminate = toolResponseMessage.getResponses().stream().anyMatch(response -> "doTerminate".equals(response.name()));
        if (isTerminate) {
            this.setState(AgentState.FINISHED);
        }
        return result;
    }


    private void outlog(AssistantMessage assistantMessage, List<AssistantMessage.ToolCall> toolCallList) {
        // ai 大模型的思考结果
        String thinkTextResult = assistantMessage.getText();
        log.info(getName() + "的思考: " + thinkTextResult);
        int toolSize = toolCallList == null ? 0 : toolCallList.size();
        log.info(getName() + "选择了" + toolSize + " 个工具来使用");

        if (toolCallList != null) {
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s",
                            toolCall.name(),
                            toolCall.arguments())
                    )
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
        }
    }


}
