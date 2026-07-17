package com.xiangzi.xiangziaiagent.demo.invoke;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONArray;

public class ChatCompletionExample {
    public static void main(String[] args) {
        // 请求URL
        String url = "https://api.openstarry.com/v1/chat/completions";
        
        // 构建请求体
        JSONObject body = new JSONObject();
        body.set("model", "glm-5.1");
        
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.set("role", "user");
        message.set("content", "你是谁？用的什么模型？");
        messages.add(message);
        
        body.set("messages", messages);
        body.set("max_tokens", 1024);
        
        // 发送POST请求
        String result = HttpUtil.createPost(url)
                .header("Authorization", "Bearer sk-openstarry-eWAhZSEsmkzxZSgUjTAYxPabAGrZ2rBcSYRoNbEMHFNe9ZHr")
                .header("Content-Type", "application/json")
                .body(body.toString())
                .execute()
                .body();
        
        // 输出响应结果
        System.out.println(result);
    }
}