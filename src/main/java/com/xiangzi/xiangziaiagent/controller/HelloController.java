package com.xiangzi.xiangziaiagent.controller;

import com.xiangzi.xiangziaiagent.demo.invoke.HttpAiInvoke;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "测试接口", description = "用于测试的接口")
public class HelloController {

    private final HttpAiInvoke qwenService;

    @GetMapping("/hello")
    @Operation(summary = "Hello 接口", description = "返回欢迎信息")
    public String hello() {
        return "Hello, Xiangzi AI Agent!";
    }

    @GetMapping("/chat")
    @Operation(summary = "通义千问对话", description = "调用阿里云通义千问 API 进行对话")
    public String chat(@RequestParam(defaultValue = "你是谁？") String message) {
        return qwenService.chat(message);
    }
}
