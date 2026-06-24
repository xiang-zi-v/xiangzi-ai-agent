package com.xiangzi.xiangziaiagent.etl;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyTokenTextSplitter {

    /**
     * 使用默认配置的TokenTextSplitter分割文档列表
     * 
     * @param documents 待分割的文档列表
     * @return 分割后的文档列表
     */
    public List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }

    /**
     * 使用自定义配置的TokenTextSplitter分割文档列表
     * 
     * 配置参数：
     * - chunkSize: 200（每个块的token大小）
     * - chunkOverlap: 100（块之间的重叠token数）
     * - maxTokensPerChunk: 10（每个块的最大token数）
     * - maxDocumentLength: 5000（最大文档长度）
     * - keepSeparator: true（保持分隔符）
     * 
     * @param documents 待分割的文档列表
     * @return 分割后的文档列表
     */
    public List<Document> splitCustomized(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter(200, 100, 10, 5000, true);
        return splitter.apply(documents);
    }


}