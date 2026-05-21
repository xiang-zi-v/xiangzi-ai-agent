package com.xiangzi.xiangziaiagent.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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


}