package com.xiangzi.xiangziaiagent.agent;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.xiangzi.xiangziaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 * <p>
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 */
@Data
@Slf4j
public abstract class BaseAgent {
    // agent 名称
    private String name;

    // 系统提示词
    private String sysPrompt;

    // 下一步提示词
    private String nextStepPrompt;

    // agent 状态（volatile 保证取消线程的写入对工作线程立即可见）
    private volatile AgentState state = AgentState.IDLE;

    // 当前步骤
    private int currentStep = 0;

    // 最大步骤
    private int maxStep = 10;

    // ai 大模型客户端实例
    private ChatClient chatClient;

    // memory 列表，用于存储 agent 与 user 交互的记录
    private List<Message> messagesList = new ArrayList<>();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        if (state != AgentState.IDLE) {
            throw new RuntimeException("can not run agent from state " + this.state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("can not run agent with userPrompt is empty");
        }
        // 更新状态
        this.state = AgentState.RUNNING;

        this.currentStep = 0;
        // 记录当前用户第一条消息
        this.messagesList.add(new UserMessage(userPrompt));

        // 执行结果列表
        List<String> results = new ArrayList<>();

        try {

            // 如果当前的状态没有完成，且当前的步骤没有超过最大步骤，继续执行
            while (this.currentStep < this.maxStep && this.state != AgentState.FINISHED && this.state != AgentState.CANCELLED) {
                this.currentStep++;
                // 获取每一步的结果
                String stepResult = this.step(null);
                // 检查是否在 step 执行期间被取消
                if (this.state == AgentState.CANCELLED) {
                    break;
                }
                // 每一步 step 执行完都要检查是否陷入循环
                if (isStuck()) {
                    handleStuckState();
                }
                String result = "Step " + this.currentStep + ": " + stepResult;
                // 把每一步的结果加入到结果列表
                results.add(result);
            }
            if (this.currentStep >= this.maxStep) {
                this.state = AgentState.FINISHED;
                results.add("Terminated Reached max steps (" + this.maxStep + ")");
            }

            return String.join("\n", results);
        } catch (Exception e) {
            log.error("run agent error", e);
            return "执行错误：" + e.getMessage();
        } finally {
            this.clearUp();
        }
    }


    /**
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return SseEmitter实例
     */
    public SseEmitter runStream(String userPrompt) {
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时
        try {
            if (state != AgentState.IDLE) {
                emitter.send("can not run agent from state " + this.state);
                emitter.complete();
                return emitter;
            }
            if (StrUtil.isBlank(userPrompt)) {
                emitter.send("can not run agent with userPrompt is empty");
                emitter.complete();
                return emitter;
            }
        } catch (IOException e) {
            emitter.completeWithError(e);
            emitter.complete();
        }

        // 更新状态
        this.state = AgentState.RUNNING;
        this.currentStep = 0;
        // 记录当前用户第一条消息
        this.messagesList.add(new UserMessage(userPrompt));

        ThreadUtil.execAsync(() -> {
            try {
                // 如果当前的状态没有完成，且当前的步骤没有超过最大步骤，继续执行
                while (this.currentStep < this.maxStep && this.state != AgentState.FINISHED && this.state != AgentState.CANCELLED) {
                    this.currentStep++;
                    // 获取每一步的结果
                    String stepResult = this.step(emitter);
                    // 检查是否在 step 执行期间被取消
                    if (this.state == AgentState.CANCELLED) {
                        break;
                    }
                    // 每一步 step 执行完都要检查是否陷入循环
                    if (isStuck()) {
                        handleStuckState();
                    }
                    String result = "Step " + this.currentStep + ": " + stepResult;
//                    emitter.send(result);
                }
                if (this.currentStep >= this.maxStep) {
                    this.state = AgentState.FINISHED;
                    emitter.send("Terminated Reached max steps (" + this.maxStep + ")");
                }
                emitter.complete();
            } catch (Exception e) {
                if (this.state == AgentState.CANCELLED) {
                    // 用户主动取消，无需报错
                    return;
                }
                state = AgentState.ERROR;
                log.error("执行智能体失败", e);
                try {
                    emitter.send("执行错误: " + e.getMessage());
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            } finally {
                this.clearUp();
            }
        });

        return emitter;
    }


    public abstract String step(SseEmitter emitter);

    public void clearUp() {
        this.messagesList.clear();
    }

    private int duplicateThreshold = 3;

    /**
     * 处理陷入循环的状态
     */
    protected void handleStuckState() {
        String stuckPrompt = "观察到重复响应。考虑新策略，避免重复已尝试过的无效路径。";
        this.nextStepPrompt = stuckPrompt + "\n" + (this.nextStepPrompt != null ? this.nextStepPrompt : "");
        System.out.println("Agent detected stuck state. Added prompt: " + stuckPrompt);
    }

    /**
     * 检查代理是否陷入循环
     *
     * @return 是否陷入循环
     */
    protected boolean isStuck() {
        List<Message> messages = this.messagesList;
        if (messages.size() < 2) {
            return false;
        }

        Message lastMessage = messages.getLast();
        if (lastMessage.getText() == null || lastMessage.getText().isEmpty()) {
            return false;
        }

        // 计算重复内容出现次数
        int duplicateCount = 0;
        for (int i = messages.size() - 2; i >= 0; i--) {
            Message msg = messages.get(i);
            if (msg.getMessageType() == MessageType.ASSISTANT &&
                    lastMessage.getText().equals(msg.getText())) {
                duplicateCount++;
            }
        }

        return duplicateCount >= this.duplicateThreshold;
    }


}
