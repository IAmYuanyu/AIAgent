package com.yuanyu.aiagent.rag;

import com.alibaba.cloud.ai.document.TextDocumentParser;
import com.alibaba.cloud.ai.reader.github.GitHubDocumentReader;
import com.alibaba.cloud.ai.reader.github.GitHubResource;
import com.yuanyu.aiagent.tool.GitHubUrlParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GitHub 仓库文档加载器
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubRepoDocumentLoader {


    /**
     * 读取 GitHub 仓库所有文档
     * @param url GitHub 仓库或目录的 URL
     * @param githubToken GitHub 访问令牌
     * @return 文档列表
     */
    public List<Document> loadDocuments(String url, String githubToken) {
        try {
            // 1. 解析 URL
            GitHubUrlParser parser = new GitHubUrlParser(url);
            String owner = parser.getOwner();
            String repo = parser.getRepo();
            String branch = parser.getBranch();
            String path = parser.getPath();

            // 2. 构建 GitHub 客户端
            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(githubToken)
                    .build();

            // 3. 递归读取目录内容
            GHRepository repository = gitHub.getRepository(owner + "/" + repo);
            List<Document> allDocuments = new ArrayList<>();
            recursiveLoad(repository, branch, path, gitHub, allDocuments);
            return allDocuments;

        } catch (IOException e) {
            log.error("读取 GitHub 仓库失败: {}", url, e);
            return Collections.emptyList();
        } catch (IllegalArgumentException e) {
            log.error("GitHub URL 解析失败: {}", url, e);
            throw e;
        }
    }

    /**
     * 递归加载文件
     */
    private void recursiveLoad(GHRepository repository, String branch, String path, GitHub gitHub, List<Document> allDocuments) throws IOException {
        List<GHContent> contents;
        try {
            if (branch != null && !branch.isBlank()) {
                contents = repository.getDirectoryContent(path, branch);
            } else {
                contents = repository.getDirectoryContent(path);
            }
        } catch (IOException e) {
            // 如果 path 是文件而不是目录，kohsuke 会报错，此时尝试直接作为文件读取
            loadSingleFile(repository, branch, path, gitHub, allDocuments);
            return;
        }

        for (GHContent content : contents) {
            if (content.isDirectory()) {
                recursiveLoad(repository, branch, content.getPath(), gitHub, allDocuments);
            } else if (content.isFile()) {
                loadSingleFile(repository, branch, content.getPath(), gitHub, allDocuments);
            }
        }
    }

    /**
     * 加载单个文件
     */
    private void loadSingleFile(GHRepository repository, String branch, String path, GitHub gitHub, List<Document> allDocuments) {
        try {
            GitHubResource resource;
            if (branch != null && !branch.isBlank()) {
                resource = GitHubResource.builder()
                        .gitHub(gitHub)
                        .owner(repository.getOwnerName())
                        .repo(repository.getName())
                        .path(path)
                        .branch(branch)
                        .build();
            } else {
                resource = GitHubResource.builder()
                        .gitHub(gitHub)
                        .owner(repository.getOwnerName())
                        .repo(repository.getName())
                        .path(path)
                        .build();
            }

            GitHubDocumentReader reader = new GitHubDocumentReader(resource, new TextDocumentParser());
            allDocuments.addAll(reader.read());
        } catch (Exception e) {
            log.error("加载文件失败: {}", path, e);
        }
    }
}
