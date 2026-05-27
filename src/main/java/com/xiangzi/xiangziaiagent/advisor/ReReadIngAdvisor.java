package com.xiangzi.xiangziaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

/**
 * ReReadIngAdvisor 重新读取输入
 * 可以提高大模型的推理能力 ，但是会增加推理时间，因为需要重新读取输入，会消耗更多的token
 */
@Slf4j
public class ReReadIngAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


    private AdvisedRequest before(AdvisedRequest request) {

        Map<String, Object> adviseUserParams = new HashMap<>(request.userParams());
        adviseUserParams.put("re2_input_query", request.userText());

        return AdvisedRequest.from(request)
                .userText("""
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """)
                .userParams(adviseUserParams)
                .build();
    }


    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);

        // 可以在advise中共享状态， 可以在context中添加一些信息，比如用户id，会话id等
        advisedRequest = advisedRequest.updateContext(context -> {
            context.put("key", "custom value");
            return context;
        });

        return chain.nextAroundCall(advisedRequest);
    }

    /*@Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }*/

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return Mono.just(advisedRequest)
                .publishOn(Schedulers.boundedElastic())
                .map(request -> {
                    // 请求前处理逻辑
                    return modifyRequest(request);
                })
                .flatMapMany(request -> chain.nextAroundStream(request))
                .map(response -> {
                    // 响应处理逻辑
                    return modifyResponse(response);
                });
    }


    private AdvisedRequest modifyRequest(AdvisedRequest request) {
        return before(request);
    }


    private AdvisedResponse modifyResponse(AdvisedResponse response) {
        return response;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
