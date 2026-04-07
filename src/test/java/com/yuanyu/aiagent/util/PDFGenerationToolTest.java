package com.yuanyu.aiagent.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "PDF生成测试.pdf";
        String content = "PDF生成测试...";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}