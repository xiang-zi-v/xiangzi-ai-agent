package com.xiangzi.xiangziaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LoveAppRagCloudConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;


/*    void test() {
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(chatClientBuilder.build().mutate())
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();

        String answer = chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(question)
                .call()
                .content();

    }*/


    /**
     * 创建阿里云DashScope云端RAG顾问Bean
     * 该方法构建一个基于DashScope云服务的检索增强顾问，用于从云端向量数据库中检索相关文档
     *
     * @return RetrievalAugmentationAdvisor实例，集成了DashScope云端文档检索能力
     */
    @Bean
    Advisor ragCloudAdvisor() {
        // 创建DashScope API客户端
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();

        // 创建DashScope文档检索器，配置云端向量索引名称为"恋爱知识库"
        DocumentRetriever retriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName("恋爱知识库")
                        .build());
        // 构建检索增强顾问，绑定文档检索器
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .build();
    }


}
