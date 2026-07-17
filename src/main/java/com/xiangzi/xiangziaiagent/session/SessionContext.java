package com.xiangzi.xiangziaiagent.session;

import com.xiangzi.xiangziaiagent.agent.BaseAgent;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 会话上下文，持有取消 token、SseEmitter 和 Agent 的引用，
 * 用于支持用户手动停止 AI 回复。
 */
@Data
public class SessionContext {

    private final String cancelToken;

    private final SseEmitter emitter;

    private final BaseAgent agent;

    private final long createdAt;
}
