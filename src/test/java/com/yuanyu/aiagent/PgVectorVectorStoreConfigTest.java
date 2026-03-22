package com.yuanyu.aiagent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class PgVectorVectorStoreConfigTest {

    @Resource
    VectorStore pgVectorVectorStore;

    @Test
    void test() {
        List<Document> documents = List.of(
                new Document("我觉得26年1月的恋爱番有一部超好看，叫做‘正相反的你和我’", Map.of("meta1", "meta1")),
                new Document("26年的1月番里有很火的‘葬送的芙莉莲’第二季呢"),
                new Document("26年3月22日缘鱼在学习SpringAI", Map.of("meta2", "meta2")));
        
        pgVectorVectorStore.add(documents);
        
        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("26年1月新番看什么").topK(3).build());
        Assertions.assertNotNull(results);
        System.out.println(results);
    }
}
