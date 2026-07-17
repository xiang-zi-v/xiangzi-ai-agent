package com.xiangzi.xiangziaiagent.session;

import com.xiangzi.xiangziaiagent.agent.BaseAgent;
import com.xiangzi.xiangziaiagent.agent.model.AgentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理器，用于注册和取消正在运行的 Agent 流式会话。
 * <p>
 * 前端发送 stop 请求时，通过 cancelToken 查找对应的会话并中断 SSE 输出。
 */
@Slf4j
@Component
public class SessionManager {

    private final Map<String, SessionContext> sessions = new ConcurrentHashMap<>();

    /**
     * 注册一个流式会话
     *
     * @param cancelToken 取消令牌
     * @param emitter     SseEmitter 实例
     * @param agent       正在运行的 Agent
     */
    public void register(String cancelToken, SseEmitter emitter, BaseAgent agent) {
        SessionContext context = new SessionContext(cancelToken, emitter, agent, System.currentTimeMillis());
        sessions.put(cancelToken, context);
        log.info("会话已注册: cancelToken={}", cancelToken);
    }

    /**
     * 取消指定会话的流式输出。
     * 使用原子 remove 保证幂等：多次调用同一个 cancelToken 只有第一次生效。
     *
     * @param cancelToken 取消令牌
     * @return 是否成功取消（token 存在且未被取消过）
     */
    public boolean cancel(String cancelToken) {
        SessionContext context = sessions.remove(cancelToken);
        if (context == null) {
            log.warn("未找到对应的会话: cancelToken={}", cancelToken);
            return false;
        }

        BaseAgent agent = context.getAgent();
        SseEmitter emitter = context.getEmitter();

        // 1. 设置 agent 状态为 CANCELLED，中断 while 循环和 Flux takeWhile
        agent.setState(AgentState.CANCELLED);

        // 2. 完成 SseEmitter，触发 onCompletion 回调清理资源
        try {
            emitter.complete();
        } catch (Exception e) {
            log.warn("完成 SseEmitter 时出现异常: {}", e.getMessage());
        }

        log.info("会话已取消: cancelToken={}", cancelToken);
        return true;
    }

    /**
     * 正常完成时移除会话记录（不触发取消逻辑）
     *
     * @param cancelToken 取消令牌
     */
    public void remove(String cancelToken) {
        sessions.remove(cancelToken);
        log.info("会话已移除: cancelToken={}", cancelToken);
    }
}
