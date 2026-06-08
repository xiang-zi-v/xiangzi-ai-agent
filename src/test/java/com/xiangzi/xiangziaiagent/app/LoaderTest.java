package com.xiangzi.xiangziaiagent.app;

import com.xiangzi.xiangziaiagent.rag.LoveAppDocumentLoader;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class LoaderTest {

    @Resource
    private LoveAppDocumentLoader documentLoader;



    @Test
    void testGetDocsFromPdf() {
        List<Document> docsFromPdf = documentLoader.getDocsFromPdf();

        System.out.println(docsFromPdf);


    }




}
