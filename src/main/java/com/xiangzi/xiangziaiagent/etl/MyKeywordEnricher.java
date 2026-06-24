package com.xiangzi.xiangziaiagent.etl;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文档关键词增强器组件
 * 
 * 使用大语言模型为文档自动提取并添加关键词元数据，
 * 提升文档的可检索性和语义理解能力
 */
@Component
public class MyKeywordEnricher {

    /**
     * DashScope大语言模型实例，用于关键词提取  ，会消耗更多的token ， 会增加响应时间
     */
    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 为文档列表添加关键词元数据
     * 
     * 使用KeywordMetadataEnricher为每个文档提取最多5个关键词，
     * 并将关键词作为元数据添加到文档中
     * 
     * @param documents 待增强的文档列表
     * @return 添加了关键词元数据的文档列表
     */
    public List<Document> enrichDocuments(List<Document> documents) {
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(this.dashscopeChatModel, 5);
        return enricher.apply(documents);
    }
}