package com.yuanyu.aiagent.config;

import com.yuanyu.aiagent.tool.*;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ToolRegistrationConfig {

    // @Value("${search-api.api-key}")
    // private String searchApiKey;

    @Bean
    public ToolCallback[] allTools() throws IOException {
        FileOperationTool fileOperationTool = new FileOperationTool();
        // WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool = new TerminateTool();
        MarkdownSearchTool markdownSearchTool = new MarkdownSearchTool();
        // 注册工具
        return ToolCallbacks.from(
            fileOperationTool,
            // webSearchTool,
            webScrapingTool,
            resourceDownloadTool,
            terminalOperationTool,
            pdfGenerationTool,
            terminateTool,
            markdownSearchTool
        );
    }
}