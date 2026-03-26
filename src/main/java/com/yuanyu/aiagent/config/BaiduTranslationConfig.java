package com.yuanyu.aiagent.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BaiduTranslationConfig {

    @Value("${baidu.translation.appId}")
    private String APP_ID;
    @Value("${baidu.translation.securityKey}")
    private String SECURITY_KEY;
    @Value("${baidu.translation.targetLanguage}")
    private String targetLanguage; // 默认中文

    public  String getTargetLanguage() {
        return targetLanguage != null ? targetLanguage : "zh";
    }
}
