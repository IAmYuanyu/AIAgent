package com.yuanyu.aiagent.app;

import cn.hutool.core.lang.UUID;
import com.yuanyu.aiagent.config.GitHubConfig;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GithubRepoExplainAppTest {

    @Resource
    private GitHubConfig gitHubConfig;

    @Resource
    private GithubRepoExplainApp githubRepoExplainApp;


    @Test
    void doChat() {
        String message = "这个项目是干啥的";
        String chatId = UUID.randomUUID().toString();
        String githubUrl = "https://github.com/IAmYuanyu/ScreenBrightnessManagementTool";
        String githubToken = gitHubConfig.getGithubToken();

        String result = githubRepoExplainApp.doChat(message, chatId, githubUrl, githubToken);
        Assertions.assertNotNull(result);
    }
}