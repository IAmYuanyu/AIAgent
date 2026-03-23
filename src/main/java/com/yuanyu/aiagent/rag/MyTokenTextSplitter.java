package com.yuanyu.aiagent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyTokenTextSplitter {
    public List<Document> splitWithBuilder(List<Document> documents) {
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(1000) // 每个文本块的目标大小（以 token 为单位）（默认：800）
                .withMinChunkSizeChars(400) // 每个文本块的最小大小（以字符为单位）（默认：350）
                .withMinChunkLengthToEmbed(10) // 包含块的最小长度（默认：5）
                .withMaxNumChunks(5000) // 从文本中生成的最大块数（默认：10000）
                .withKeepSeparator(true) // 是否在块中保留分隔符（如换行符）（默认：true）
                .build();

        return splitter.apply(documents);
    }
}
