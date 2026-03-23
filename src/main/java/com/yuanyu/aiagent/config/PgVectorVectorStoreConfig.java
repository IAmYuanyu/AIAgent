package com.yuanyu.aiagent.config;

import com.yuanyu.aiagent.rag.LoveAppDocumentLoader;
import com.yuanyu.aiagent.rag.MyTokenTextSplitter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
@RequiredArgsConstructor
public class PgVectorVectorStoreConfig {

    private final LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        PgVectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1024) // Optional: defaults to model dimensions or 1536
                .distanceType(COSINE_DISTANCE) // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW) // Optional: defaults to HNSW
                .initializeSchema(true) // Optional: defaults to false
                .schemaName("public") // Optional: defaults to "public"
                .vectorTableName("vector_store") // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000) // Optional: defaults to 10000
                .build();

        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();

        /*
          分批添加文档到向量存储，避免单次请求超过 API 限制
          每批次处理 10 个文档，确保向量化请求的批量大小符合规范
         */
        if (!documents.isEmpty()) {
            int batchSize = 10;
            // 循环拆分批次：i是当前批次的起始索引，每次递增batchSize（10）
            for (int i = 0; i < documents.size(); i += batchSize) {
                // 计算当前批次的结束索引：取「i+10」和「文档总数」的较小值，避免最后一批越界
                int end = Math.min(i + batchSize, documents.size());
                // 截取当前批次的文档子列表（从i到end，左闭右开）
                List<Document> batch = documents.subList(i, end);
                pgVectorStore.add(batch);
            }
        }

        return pgVectorStore;
    }
}
