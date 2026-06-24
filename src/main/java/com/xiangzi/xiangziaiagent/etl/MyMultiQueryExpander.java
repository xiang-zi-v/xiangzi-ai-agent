package com.xiangzi.xiangziaiagent.etl;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 多查询扩展器组件
 * 
 * 基于大语言模型实现查询扩展功能，将单个用户查询扩展为多个语义相似的查询，
 * 用于提升RAG系统的检索召回率
 */
@Component
public class MyMultiQueryExpander {

    /**
     * ChatClient构建器，用于创建大语言模型客户端
     */
    private final ChatClient.Builder chatClientBuilder;

    /**
     * 构造函数，注入ChatClient构建器
     * 
     * @param chatClientBuilder ChatClient构建器实例
     */
    public MyMultiQueryExpander(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    /**
     * 将单个查询扩展为多个语义相关的查询
     * 
     * 使用MultiQueryExpander将输入查询扩展为3个不同角度的查询，
     * 帮助从多个维度检索相关文档，提升检索效果
     * 
     * @param query 原始用户查询
     * @return 扩展后的查询列表，包含3个语义相关的查询
     */
    public List<Query> expand(String query) {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3)
                .build();
        return queryExpander.expand(new Query(query));
    }

}