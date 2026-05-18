package com.xiangzi.xiangziaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 阿里云通义千问 API 服务
 */
@Slf4j
@Service
public class HttpAiInvoke {

//    @Value("${dashscope.api.key:}")
    private String apiKey = TestApiKey.API_KEY;

    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    /**
     * 调用通义千问 API
     *
     * @param userMessage 用户消息
     * @return API 响应结果
     */
    public String chat(String userMessage) {
        return chat(userMessage, "qwen-plus");
    }

    /**
     * 调用通义千问 API
     *
     * @param userMessage 用户消息
     * @param model       模型名称
     * @return API 响应结果
     */
    public String chat(String userMessage, String model) {
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", model);

        // 构建 messages
        JSONObject[] messages = new JSONObject[2];
        
        // system message
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", "You are a helpful assistant.");
        messages[0] = systemMessage;

        // user message
        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", userMessage);
        messages[1] = userMsg;

        requestBody.set("input", new JSONObject().set("messages", messages));

        // 构建 parameters
        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);

        log.info("请求参数: {}", requestBody.toString());

        // 发送 HTTP 请求
        HttpResponse response = HttpRequest.post(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(30000)
                .execute();

        log.info("响应状态码: {}", response.getStatus());
        log.info("响应内容: {}", response.body());

        if (response.isOk()) {
            return response.body();
        } else {
            throw new RuntimeException("API 调用失败: " + response.body());
        }
    }

    /**
     * 调用通义千问 API（支持自定义系统提示）
     *
     * @param systemMessage 系统提示
     * @param userMessage   用户消息
     * @param model         模型名称
     * @return API 响应结果
     */
    public String chatWithSystem(String systemMessage, String userMessage, String model) {
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", model);

        // 构建 messages
        JSONObject[] messages = new JSONObject[2];
        
        // system message
        JSONObject sysMsg = new JSONObject();
        sysMsg.set("role", "system");
        sysMsg.set("content", systemMessage);
        messages[0] = sysMsg;

        // user message
        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", userMessage);
        messages[1] = userMsg;

        requestBody.set("input", new JSONObject().set("messages", messages));

        // 构建 parameters
        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);

        log.info("请求参数: {}", requestBody.toString());

        // 发送 HTTP 请求
        HttpResponse response = HttpRequest.post(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(30000)
                .execute();

        log.info("响应状态码: {}", response.getStatus());
        log.info("响应内容: {}", response.body());

        if (response.isOk()) {
            return response.body();
        } else {
            throw new RuntimeException("API 调用失败: " + response.body());
        }
    }
}
