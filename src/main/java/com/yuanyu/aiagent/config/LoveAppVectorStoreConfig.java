package com.yuanyu.aiagent.config;

import com.yuanyu.aiagent.rag.LoveAppDocumentLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 恋爱大师向量数据库配置（初始化基于内存的向量数据库Bean）
 */
@Configuration
@RequiredArgsConstructor
public class LoveAppVectorStoreConfig {

    private final LoveAppDocumentLoader loveAppDocumentLoader;

    // @Bean  TODO 暂时关掉启动时加载向量数据库（不然浪费token）
    public VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        // 加载Markdown文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        // 将文档存到向量数据库
        simpleVectorStore.doAdd(documents);
        return simpleVectorStore;
    }
}
