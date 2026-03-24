package com.yuanyu.aiagent.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoveAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    /**
     * 加载Markdown文档
     * @return
     */
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                // 根据自己的文档名字来拆，比如3个恋爱文档都是 恋爱常见问题和回答 - xx篇.md 格式就能这么拆，但实际情况一般会复杂得多
                String status = filename.substring(filename.length() - 6, filename.length() - 4);
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true) // 遇到 Markdown 的水平分割线（例如 --- / ***）时，就把它当作“文档分隔符”来切分
                        .withIncludeCodeBlock(false) // 是否添加代码块
                        .withIncludeBlockquote(false) // 是否添加块引用
                        .withAdditionalMetadata("filename", filename) // 添加元数据
                        .withAdditionalMetadata("status", status)
                        .build();
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;
    }
}
