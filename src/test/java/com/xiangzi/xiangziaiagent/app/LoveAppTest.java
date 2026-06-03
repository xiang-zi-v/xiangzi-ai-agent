package com.xiangzi.xiangziaiagent.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class LoveAppTest {


    @Resource
    private LoveApp loveApp;


    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是祥子";
        String answer = loveApp.doChat(message, chatId);
    }

    @Test
    void coChatConverter() {
        String chatId = UUID.randomUUID().toString();
        loveApp.coChatConverter("Generate 5 movies for Tom Hanks.");
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是祥子";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);

    }

    @Test
    void doChatUploadFile() throws IOException {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是祥子";
        String response = loveApp.doChatUploadFile(message, chatId);
    }

    @Test
    void doChatWithVectorStore() {
        String chatId = UUID.randomUUID().toString();
        String prompt = "恋爱中如何有效处理双方的争吵？";
        String response = loveApp.doChatWithVectorStore(prompt, chatId);
    }



}