package com.xiangzi.xiangziaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.transformer.SummaryMetadataEnricher;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

@Component
class MyDocumentEnricher {

    private final ChatModel chatModel;

    MyDocumentEnricher(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    // 关键词元信息增强器
    List<Document> enrichDocumentsByKeyword(List<Document> documents) {
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(this.chatModel, 5);
        return enricher.apply(documents);
    }

    // 摘要元信息增强器
    List<Document> enrichDocumentsBySummary(List<Document> documents) {
        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS, SummaryMetadataEnricher.SummaryType.CURRENT, SummaryMetadataEnricher.SummaryType.NEXT));
        return enricher.apply(documents);
    }


    void text(Document document) {

        /*// 抽取：从 PDF 文件读取文档
        PDFReader pdfReader = new PagePdfDocumentReader("knowledge_base.pdf");
        List<Document> documents = pdfReader.read();

        // 转换：分割文本并添加摘要
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.apply(documents);

        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.CURRENT));
        List<Document> enrichedDocuments = enricher.apply(splitDocuments);

        // 加载：写入向量数据库
        vectorStore.write(enrichedDocuments);

        // 或者使用链式调用
        vectorStore.write(enricher.apply(splitter.apply(pdfReader.read())));*/

        DefaultContentFormatter formatter = DefaultContentFormatter.builder()
                .withMetadataTemplate("{key}: {value}")
                .withMetadataSeparator("\n")
                .withTextTemplate("{metadata_string}\n\n{content}")
                .withExcludedInferenceMetadataKeys("embedding", "vector_id")
                .withExcludedEmbedMetadataKeys("source_url", "timestamp")
                .build();

        // 使用格式化器处理文档
        String formattedText = formatter.format(document, MetadataMode.INFERENCE);

    }


    @Resource
    EmbeddingModel dashScopeEmbeddingModel;

    void ETL_Test() {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader("knowledge_base.pdf");
        List<Document> documents = pdfReader.read();

        // 转换：分割文本并添加摘要
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.apply(documents);

        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.CURRENT));
        List<Document> enrichedDocuments = enricher.apply(splitDocuments);



        VectorStore vectorStore = SimpleVectorStore.builder(dashScopeEmbeddingModel).build();

        // 加载：写入向量数据库
        vectorStore.write(enrichedDocuments);

        // 或者使用链式调用
        vectorStore.write(enricher.apply(splitter.apply(pdfReader.read())));

    }


}