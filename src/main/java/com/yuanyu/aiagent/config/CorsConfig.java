package com.yuanyu.aiagent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖全部请求
        registry.addMapping("/**")
                // 允许发送 cookies
                .allowCredentials(true)
                // 允许访问来源（域名）
                .allowedOriginPatterns("*")
                // 允许的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的 HTTP 头
                .allowedHeaders("*")
                // 暴露的 HTTP 头
                .exposedHeaders("*");
    }
}