package com.xiangzi.xiangziaiagent.controller;


import com.xiangzi.xiangziaiagent.agent.XiangziManus;
import com.xiangzi.xiangziaiagent.agent.model.AgentState;
import com.xiangzi.xiangziaiagent.app.FitnessApp;
import com.xiangzi.xiangziaiagent.app.LoveApp;
import com.xiangzi.xiangziaiagent.session.SessionManager;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final LoveApp loveApp;
    private final FitnessApp fitnessApp;
    private final ToolCallback[] allTools;
    private final ChatModel dashscopeChatModel;
    private final SessionManager sessionManager;

    /**
     * 同步调用LoveApp的doChat方法
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }

    /**
     * 异步调用LoveApp的doChat方法,并且添加SSE ‍对应的‍ MediaType
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppASync(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * 异步调用LoveApp的doChat方法,并且添加SSE ‍对应的‍ MediaType
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/fitness_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithFitnessAppASync(String message, String chatId) {
        return fitnessApp.doChatByStream(message, chatId);
    }


    /**
     * 2）返回 Flux 对象，并且‍‍设置泛型为 ServerSentEvent。使用这种方式可以省略 MediaType：
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/async")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppSSE(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }


    /**
     * 3）使用 SSEEmiter，‍‍通过 send 方法持续向 SseEmitter 发送消息（有点像 IO 操作）
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sse/emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        loveApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回emitter
        return emitter;
    }


    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message     用户消息
     * @param cancelToken 取消令牌（用于手动停止 AI 回复）
     * @return SseEmitter 实例
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message,
            @RequestParam(required = false, defaultValue = "") String cancelToken) {
        XiangziManus xiangziManus = new XiangziManus(allTools, dashscopeChatModel);
        SseEmitter emitter = xiangziManus.runStream(message);

        // 注册会话，支持用户手动取消
        if (StrUtil.isNotBlank(cancelToken)) {
            sessionManager.register(cancelToken, emitter, xiangziManus);
        }

        // 设置 emitter 生命周期回调（从 BaseAgent 移出，与 SessionManager 组合管理）
        emitter.onTimeout(() -> {
            if (StrUtil.isNotBlank(cancelToken)) {
                sessionManager.remove(cancelToken);
            }
            xiangziManus.setState(AgentState.ERROR);
            xiangziManus.clearUp();
            log.warn("SSE connection timed out, cancelToken={}", cancelToken);
        });

        emitter.onCompletion(() -> {
            if (StrUtil.isNotBlank(cancelToken)) {
                sessionManager.remove(cancelToken);
            }
            if (xiangziManus.getState() == AgentState.RUNNING) {
                xiangziManus.setState(AgentState.FINISHED);
            }
            xiangziManus.clearUp();
            log.info("SSE connection completed, cancelToken={}", cancelToken);
        });

        return emitter;
    }

    /**
     * 手动停止 AI 生成
     *
     * @param cancelToken 取消令牌
     * @return 操作结果
     */
    @PostMapping("/stop")
    public Map<String, Object> stopGeneration(@RequestParam String cancelToken) {
        boolean cancelled = sessionManager.cancel(cancelToken);
        return Map.of(
                "success", cancelled,
                "cancelToken", cancelToken,
                "message", cancelled ? "已停止生成" : "未找到对应的会话"
        );
    }


}
