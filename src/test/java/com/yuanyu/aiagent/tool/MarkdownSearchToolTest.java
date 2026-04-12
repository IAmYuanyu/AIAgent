package com.yuanyu.aiagent.tool;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownSearchToolTest {

    @Test
    void searchMarkdown() throws IOException {
        MarkdownSearchTool markdownSearchTool = new MarkdownSearchTool();
        String string = markdownSearchTool.searchMarkdown("SpringAI", true);
        System.out.println(string);
    }
}