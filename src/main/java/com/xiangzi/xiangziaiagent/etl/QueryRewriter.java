package com.xiangzi.xiangziaiagent.etl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 查询重写器组件
 * 
 * 使用大语言模型对用户原始查询进行语义重写和优化，
 * 生成更精确、更适合检索的查询语句，提升RAG系统的检索准确性
 */
@Component
public class QueryRewriter {

    /**
     * 查询转换器，用于执行查询重写逻辑
     */
    private final QueryTransformer queryTransformer;

    /**
     * 构造函数，初始化查询重写器
     * 
     * @param dashscopeChatModel DashScope大语言模型实例
     */
    public QueryRewriter(ChatModel dashscopeChatModel) {
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
    }

    /**
     * 执行查询重写
     * 
     * 将用户输入的原始查询转换为优化后的查询语句，
     * 增强查询的表达能力和检索效果
     * 
     * @param prompt 用户原始查询
     * @return 重写后的查询文本
     */
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);
        Query transformedQuery = queryTransformer.transform(query);
        return transformedQuery.text();
    }
}