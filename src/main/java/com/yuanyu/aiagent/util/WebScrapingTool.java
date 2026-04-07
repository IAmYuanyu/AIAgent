package com.yuanyu.aiagent.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

public class WebScrapingTool {

    @Tool(description = "抓取网页的内容")
    public String scrapeWebPage(@ToolParam(description = "需要抓取网页的URL地址") String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.html();
        } catch (IOException e) {
            return "页面抓取失败，错误信息: " + e.getMessage();
        }
    }
}