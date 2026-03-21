package com.yuanyu.aiagent.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义基于阿里云知识库服务的RAG增强顾问
 */
@Configuration
public class LoveAppRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public Advisor loveAppRagCloudAdvisor() {
        // 初始化DashScope API客户端，使用配置的API密钥进行身份验证
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(apiKey).build();
        // 创建文档检索器，连接到名为"恋爱大师"的知识库索引
        DashScopeDocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .indexName("恋爱大师").build());
        // 构建并返回检索增强型顾问实例，整合文档检索功能，使AI代理能够结合实时检索的知识库内容生成回答
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(dashScopeDocumentRetriever)
                .build();
    }
}
