package com.yuanyu.aiagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Github 访问令牌加载器
 */
@Configuration
public class GitHubConfig {

    @Value("${github.token:}")
    private String githubToken;

    public String getGithubToken() {
        return githubToken;
    }
}
