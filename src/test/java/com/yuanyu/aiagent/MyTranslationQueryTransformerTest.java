package com.yuanyu.aiagent;

import com.yuanyu.aiagent.config.BaiduTranslationConfig;
import com.yuanyu.aiagent.rag.MyTranslationQueryTransformer;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyTranslationQueryTransformerTest {

    @Resource
    BaiduTranslationConfig baiduTranslationConfig;

    @Test
    void test() {
        String appId = baiduTranslationConfig.getAPP_ID();
        String securityKey = baiduTranslationConfig.getSECURITY_KEY();
        String targetLanguage = baiduTranslationConfig.getTargetLanguage();

        MyTranslationQueryTransformer myTranslationQueryTransformer = MyTranslationQueryTransformer.builder()
                .targetLanguage(targetLanguage)
                .APP_ID(appId)
                .SECURITY_KEY(securityKey)
                .build();

        Query query = myTranslationQueryTransformer.transform(new Query("Hello, world!What can I say?"));
        System.out.println(query.text());
    }
}
