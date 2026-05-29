package com.xiangzi.xiangziaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;


/**
 * MyLoggerAdvisor 日志记录器
 * @author xiangzi
 * @since 1.0.0
 */

@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 请求之前
     */
    private AdvisedRequest before(AdvisedRequest request) {
        log.info("AI request:{}", request.userText());
        return request;
    }

    /**
     * 请求之后
     */
    private void after(AdvisedResponse response) {
        log.info("AI response:{}", response.response().getResult().getOutput().getText());
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        this.after(advisedResponse);
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {

        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(advisedRequest);

        // 通过 MessageAggregator工具类将Flux响应聚合成一个单个的AdvisedResponse，每个AdvisedResponse包含一个Message
        // 不能在 MessageAggregator 中修改响应，因为他是只读的
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponseFlux, this::after);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
