package com.xiangzi.xiangziaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LoveAppDocumentLoader {

    /**
     * 资源模式解析器，用于解析 classpath 下的文档资源
     */
    private final ResourcePatternResolver resourcePatternResolver;


    /**
     * LoveAppDocumentLoader 构造函数
     *
     * @param resourcePatternResolver 资源模式解析器，用于解析 classpath 下的文档资源
     */
    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadDocuments() {
        List<Document> documents = new ArrayList<>();
        try {
            Resource[] resources = this.resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                // 配置 MarkdownDocumentReader 读取文档配置
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true) // 使用水平规则创建文档
                        .withIncludeCodeBlock(false) // 包含代码块
                        .withIncludeBlockquote(false) // 包含引用块
                        .withAdditionalMetadata("filename", filename) // 添加文件名元数据(元信息)
                        .build();
                // 创建 MarkdownDocumentReader 读取文档实例
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                documents.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("加载文档失败", e);
        }
        return documents;
    }

}