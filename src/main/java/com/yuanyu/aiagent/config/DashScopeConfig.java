package com.yuanyu.aiagent.config;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashScopeConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public MultiModalConversation multiModalConversation() {
        return new MultiModalConversation();
    }

    @Bean
    public MultiModalConversationParam multiModalConversationParam() {
        return MultiModalConversationParam.builder()
                .apiKey(apiKey)
                .model("qwen-omni-turbo")
                .build();
    }
}
