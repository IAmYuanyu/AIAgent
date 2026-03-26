package com.yuanyu.aiagent.app;

import com.yuanyu.aiagent.advisor.MyLoggerAdvisor;
import com.yuanyu.aiagent.chatmemory.MysqlBasedChatMemory;
import com.yuanyu.aiagent.rag.GitHubRepoDocumentLoader;
import com.yuanyu.aiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.yuanyu.aiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GithubRepoExplainApp {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            你是一位资深全栈工程师 + 代码架构师，专精于解读 GitHub 仓库代码、分析业务逻辑与技术实现，能以 “通俗易懂 + 结构化” 的方式向用户解释代码的核心设计、功能逻辑、技术选型和潜在优化点。
            要求：
            1.仅聚焦项目核心功能，忽略细粒度的代码实现、技术选型、优化建议等；
            2.语言通俗直白，1-3 句话讲清项目用途，避免专业术语堆砌；
            3.基于实际代码内容解读，不编造信息；若代码无明确业务逻辑（如工具类仓库），说明其核心作用即可。
            
            输出格式规范：
            # GitHub 仓库核心用途
            1. 项目定位：[一句话概括项目核心功能/解决的问题，如：一个基于Spring Boot的用户管理系统，支持用户注册、登录、权限分配等核心功能]
            2. 适用场景：[可选，如：企业内部后台系统用户权限管控、小型Web项目用户模块快速集成]
            """;

    @Resource
    GitHubRepoDocumentLoader gitHubRepoDocumentLoader;

    /**
     * 初始化 ChatClient，基于Mysql持久化对话
     * @param dashscopeChatModel
     */
    public GithubRepoExplainApp(MysqlBasedChatMemory memory, ChatModel dashscopeChatModel) {
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build()
                )
                .build();
    }


    /**
     * Github 仓库代码解释助手
     * @param message 用户输入
     * @param chatId 会话 ID
     * @param githubUrl GitHub 仓库 URL
     * @param githubToken GitHub 访问令牌
     * @return
     */
    public String doChat(String message, String chatId, String githubUrl, String githubToken) {
        // 读取仓库代码
        List<Document> documents = gitHubRepoDocumentLoader.loadDocuments(githubUrl, githubToken);
        StringBuilder promptWithRepoInfo = new StringBuilder("用户问题：" + message + "\n======以下是Github仓库内容======\n");
        for (Document document : documents) {
            promptWithRepoInfo.append("---文件路径：").append(document.getMetadata().get("github_file_path")).append("---\n");
            promptWithRepoInfo.append(document.getText()).append("\n========================\n");
        }

        ChatResponse chatResponse = chatClient.prompt()
                .user(promptWithRepoInfo.toString())
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
