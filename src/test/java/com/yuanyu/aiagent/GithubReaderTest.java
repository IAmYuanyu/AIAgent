package com.yuanyu.aiagent;

import com.yuanyu.aiagent.config.GitHubConfig;
import com.yuanyu.aiagent.rag.GitHubRepoDocumentLoader;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class GithubReaderTest {


    @Resource
    private GitHubConfig gitHubConfig;

    @Test
    void test() {
        GitHubRepoDocumentLoader gitHubRepoDocumentLoader = new GitHubRepoDocumentLoader();
        List<Document> documentList = gitHubRepoDocumentLoader.loadDocuments("https://github.com/IAmYuanyu/AIAgent/tree/main/src/test/java/com/yuanyu/aiagent/demo/invoke",
                gitHubConfig.getGithubToken());

        for (Document doc : documentList) {
            System.out.println("========= 文件路径：" + doc.getMetadata().get("github_file_path") + " =========");
            System.out.println(doc.getText());
            System.out.println("----------------------------------------");
        }
    }
}
