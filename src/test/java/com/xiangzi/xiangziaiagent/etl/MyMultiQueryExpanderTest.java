package com.xiangzi.xiangziaiagent.etl;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyMultiQueryExpanderTest {
    @Resource
    private MyMultiQueryExpander myMultiQueryExpander;
    @Resource
    private VectorStore loveAppVectorStore;

    @Test
    void expand() {
        List<Query> queries = myMultiQueryExpander.expand("谁是程序员鱼皮啊？");
        assertTrue(queries.size() > 0);
        Map<Query, List<List<Document>>> documentsForQuery = new HashMap<>();

        for (Query query : queries) {
            DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                    .vectorStore(loveAppVectorStore)
                    .similarityThreshold(0.73)
                    .topK(5)
                    .filterExpression(new FilterExpressionBuilder()
                            .eq("genre", "fairytale")
                            .build())
                    .build();

            // 直接用扩展后的查询来获取文档
            List<Document> retrievedDocuments = retriever.retrieve(new Query("谁是程序员鱼皮啊？"));
            documentsForQuery.put(query, List.of(retrievedDocuments));
        }

        // 创建文档合并器实例
        DocumentJoiner documentJoiner = new ConcatenationDocumentJoiner();

        // 执行文档合并并去重
        List<Document> documents = documentJoiner.join(documentsForQuery);

    }


    @Test
    void test() {
        DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(loveAppVectorStore)
                .similarityThreshold(0.73)
                .topK(5)
                .filterExpression(new FilterExpressionBuilder()
                        .eq("genre", "fairytale")
                        .build())
                .build();

        // 直接用扩展后的查询来获取文档
        List<Document> retrievedDocuments = retriever.retrieve(new Query("谁是程序员鱼皮啊？"));
        // 输出扩展后的查询文本
        System.out.println(retrievedDocuments);
    }


}