package com.xiangzi.xiangziaiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * 网页爬取工具类
 * 基于 Spring AI Tool 框架，允许 AI Agent 通过自然语言指令
 * 抓取指定 URL 的网页 HTML 内容。
 */
public class WebScrapingTool {

    /**
     * 抓取指定 URL 网页的 HTML 内容
     * 内部使用 Jsoup 库建立 HTTP 连接并解析页面，最终返回页面的完整 HTML。
     *
     * @param url 要抓取的网页 URL（例如 "https://www.example.com"）
     * @return 抓取成功返回页面的 HTML 文本；失败时返回包含错误信息的字符串
     */
    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            // 使用 Jsoup 连接目标 URL 并获取页面文档对象
            Document doc = Jsoup.connect(url).get();
            // 返回页面完整的 HTML 内容
            return doc.html();
        } catch (IOException e) {
            // 捕获 IO 异常（如网络错误、URL 无效等）并返回友好的错误提示
            return "Error scraping web page: " + e.getMessage();
        }
    }
}