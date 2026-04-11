package com.yuanyu.aiagent.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * GitHub URL 解析器，获取 owner 和 repo
 */
public class GitHubUrlParser {
    private static final Logger log = LoggerFactory.getLogger(GitHubUrlParser.class);

    private final String owner;
    private final String repo;
    private final String branch; // tree/blob
    private final String path;

    /**
     * 构造器：解析 GitHub 链接，失败则抛出异常
     * @param githubUrl GitHub 仓库链接（如 https://github.com/owner/repo/tree(branch)/main/src）
     * @throws IllegalArgumentException 链接为空/格式错误/分段不足时抛出
     */
    public GitHubUrlParser(String githubUrl) {
        Objects.requireNonNull(githubUrl, "GitHub 链接不能为空");
        if (githubUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub 链接不能为空白字符串");
        }

        ParseResult parseResult = parseUrl(githubUrl);
        this.owner = parseResult.owner;
        this.repo = parseResult.repo;
        this.branch = parseResult.branch;
        this.path = parseResult.path;
    }

    /**
     * 内部解析方法，封装解析逻辑
     * @param githubUrl 待解析链接
     * @return 解析结果（包含 owner/repo/branch/path）
     * @throws IllegalArgumentException 解析失败时抛出明确异常
     */
    private ParseResult parseUrl(String githubUrl) {
        try {
            // 1. 解析 URL
            URL url = new URL(githubUrl);
            // 额外校验：确保域名是 github.com
            String host = url.getHost();
            if (!host.equalsIgnoreCase("github.com") && !host.equalsIgnoreCase("www.github.com")) {
                throw new IllegalArgumentException("非 GitHub 域名：" + host + "，仅支持 github.com 链接");
            }

            // 2. 获取路径部分（如 "/Owner/Repo/branch(tree或blob)/path"）
            String urlPath = url.getPath();

            // 3. 拆分路径
            String[] pathSegments = urlPath.split("/");
            String[] validSegments = Arrays.stream(pathSegments)
                    .filter(segment -> !segment.isEmpty())
                    .toArray(String[]::new);

            // 4. 提取信息
            if (validSegments.length >= 2) {
                String owner = validSegments[0].trim();
                String repo = validSegments[1].trim();
                String branch = null;
                String path = "";

                // 判断是否包含 tree 或 blob (GitHub 分支/文件路径标识)
                if (validSegments.length >= 4 && (validSegments[2].equals("tree") || validSegments[2].equals("blob"))) {
                    branch = validSegments[3].trim();
                    if (validSegments.length > 4) {
                        StringBuilder pathBuilder = new StringBuilder();
                        for (int i = 4; i < validSegments.length; i++) {
                            pathBuilder.append(validSegments[i]);
                            if (i < validSegments.length - 1) {
                                pathBuilder.append("/");
                            }
                        }
                        path = pathBuilder.toString();
                    }
                }

                if (owner.isBlank() || repo.isBlank()) {
                    throw new IllegalArgumentException("解析失败：owner 或 repo 为空");
                }
                return new ParseResult(owner, repo, branch, path);
            } else {
                throw new IllegalArgumentException("解析失败：路径分段不足，无法提取 owner 和 repo，链接：" + githubUrl);
            }

        } catch (MalformedURLException e) {
            log.error("GitHub 链接格式错误：{}", githubUrl, e);
            throw new IllegalArgumentException("GitHub 链接格式非法：" + githubUrl, e);
        }
    }

    // 内部静态类：封装解析结果
    private static class ParseResult {
        final String owner;
        final String repo;
        final String branch;
        final String path;

        ParseResult(String owner, String repo, String branch, String path) {
            this.owner = owner;
            this.repo = repo;
            this.branch = branch;
            this.path = path;
        }
    }

    public String getOwner() {
        return owner;
    }

    public String getRepo() {
        return repo;
    }

    public String getBranch() {
        return branch;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "GitHubUrlParser{" +
                "owner='" + owner + '\'' +
                ", repo='" + repo + '\'' +
                ", branch='" + branch + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
