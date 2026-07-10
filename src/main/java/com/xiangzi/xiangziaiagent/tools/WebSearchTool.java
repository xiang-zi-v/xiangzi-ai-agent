package com.xiangzi.xiangziaiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Web 搜索工具类
 *
 * 通过 SearchAPI 调用 Baidu 搜索引擎，为 Spring AI 提供可被 Agent 调用的 Tool 能力。
 * 类被 Spring AI 扫描后，searchWeb 方法可作为工具函数由大模型自主调用以获取外部信息。
 */
public class WebSearchTool {

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    // SearchAPI 的访问密钥，用于接口鉴权
    private final String apiKey;

    /**
     * 构造函数：注入 API Key
     *
     * @param apiKey SearchAPI 平台的授权密钥
     */
    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 使用 Baidu 搜索引擎检索信息
     *
     * 该方法被 {@link Tool} 标记，会被 Spring AI 注册为工具，
     * 大模型可通过传入搜索关键词触发一次联网搜索。
     *
     * @param query 搜索关键词
     * @return 搜索结果前 5 条，以 JSON 数组字符串的形式拼接返回；请求失败时返回错误描述
     */
    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
        @ToolParam(description = "Search query keyword") String query) {
        // 组装请求参数：关键词、API 密钥、搜索引擎类型
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");
        try {
            // 通过 HTTP GET 请求 SearchAPI 并获取响应
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            // 解析 JSON 响应，取出 organic_results（自然搜索结果）并截取前 5 条
            JSONObject jsonObject = JSONUtil.parseObj(response);
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            List<Object> objects = organicResults.subList(0, 5);
            // 将每条结果转换为 JSON 字符串，并用逗号拼接成一个字符串返回
            String result = objects.stream().map(obj -> {
                JSONObject tmpJSONObject = (JSONObject) obj;
                return tmpJSONObject.toString();
            }).collect(Collectors.joining(","));
            return result;
        } catch (Exception e) {
            // 异常兜底：搜索失败时返回错误提示信息
            return "Error searching Baidu: " + e.getMessage();
        }
    }
}