package com.xiangzi.xiangziaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
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
                .withProxyToolCalls(true)
                .build();
    }

    @Override
    public boolean think() {
        if (StrUtil.isNotBlank(this.getNextStepPrompt())) {
            // 大模型是没有记忆的，每一次思考都要重新定思考的提示词
            UserMessage userMessage = new UserMessage(this.getNextStepPrompt());
            this.getMessagesList().add(userMessage);
        }
        try {
            List<Message> messageList = this.getMessagesList();
            Prompt prompt = new Prompt(messageList, this.chatOptions);

            // 获取大模型思考结果
            ChatResponse chatResponse = this.getChatClient()
                    .prompt(prompt)
                    .system(this.getSysPrompt())
                    .tools(this.availableTools)
                    .call()
                    .chatResponse();

            // 保存大模型的响应
            this.toolCallChatResponse = chatResponse;

            // 大模型的思考结果
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 大模型调用的工具列表
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();

            // 输出日志
            this.outlog(assistantMessage, toolCallList);

            if (toolCallList.isEmpty()) {
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


    @Override
    public String act() {
        if (!this.toolCallChatResponse.hasToolCalls()) {
            return "没有工具可以调用";
        }

        // todo




        return "";
    }


    private void outlog(AssistantMessage assistantMessage, List<AssistantMessage.ToolCall> toolCallList) {
        // ai 大模型的思考结果
        String thinkTextResult = assistantMessage.getText();
        log.info(getName() + "的思考: " + thinkTextResult);
        log.info(getName() + "选择了 " + toolCallList.size() + " 个工具来使用");

        String toolCallInfo = toolCallList.stream()
                .map(toolCall -> String.format("工具名称：%s，参数：%s",
                        toolCall.name(),
                        toolCall.arguments())
                )
                .collect(Collectors.joining("\n"));
        log.info(toolCallInfo);
    }




}
