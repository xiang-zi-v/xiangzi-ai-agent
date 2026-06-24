package com.xiangzi.xiangziaiagent.rag;

import com.xiangzi.xiangziaiagent.etl.MyKeywordEnricher;
import com.xiangzi.xiangziaiagent.etl.MyTokenTextSplitter;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;

@Configuration
public class LoveAppVectorStoreConfig {

    /**
     * LoveApp文档加载器，用于加载需要向量化的文档数据
     */
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;
    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;
    @Resource
    private MyKeywordEnricher myKeywordEnricher;


    /**
     * 创建LoveApp向量存储Bean
     * 该方法构建一个SimpleVectorStore，加载文档并将其向量化存储
     *
     * @return 已加载文档的VectorStore实例
     */
    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashScopeEmbeddingModel) {
        // 使用DashScope嵌入模型构建SimpleVectorStore
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashScopeEmbeddingModel)
                .build();
        // 加载文档列表
        List<Document> documents = loveAppDocumentLoader.loadDocuments();

//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents); // 分割文档

//        List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documents); // AI 提取关键词

        // 将文档添加到向量存储中
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }

}