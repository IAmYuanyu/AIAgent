package com.yuanyu.aiagent.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

public class MarkdownSearchTool {

    private static final int CONTEXT_SIZE = 200;
    private final String directory = new ClassPathResource("static/post").getFile().getAbsolutePath();

    public MarkdownSearchTool() throws IOException {
        Path dirPath = Paths.get(directory);
        if (!Files.exists(dirPath)) {
            throw new IOException("目录不存在：" + directory);
        }
    }

    @Tool(description = "根据关键词查询缘鱼博客文章，返回博客中匹配到关键词的部分，每个匹配到的部分所在文章标题用[]包裹")
    public String searchMarkdown(
            @ToolParam(description = "要搜索的关键词，每次只能传入一个关键词，不支持正则表达式") String keyword,
            @ToolParam(description = "是否忽略大小写，默认 true") Boolean ignoreCase) {
        if (keyword == null || keyword.isBlank()) {
            return "关键词不能为空";
        }
        boolean ignore = ignoreCase == null || ignoreCase;
        Path basePath = Paths.get(directory);
        if (!Files.exists(basePath) || !Files.isDirectory(basePath)) {
            return "目录不存在或不可访问：" + directory;
        }

        StringBuilder result = new StringBuilder();
        String searchKeyword = ignore ? keyword.toLowerCase(Locale.ROOT) : keyword;

        try (Stream<Path> pathStream = Files.walk(basePath)) {
            pathStream.filter(Files::isRegularFile)
                    .filter(MarkdownSearchTool::isMarkdownFile)
                    .forEach(path -> searchInFile(path, searchKeyword, keyword, ignore, result));
        } catch (IOException e) {
            return "搜索失败：" + e.getMessage();
        }

        if (result.length() == 0) {
            return "未找到匹配内容";
        }
        return result.toString();
    }

    private static boolean isMarkdownFile(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".md") || name.endsWith(".markdown");
    }

    private static void searchInFile(Path path, String searchKeyword, String originalKeyword, boolean ignore, StringBuilder result) {
        try {
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            String searchContent = ignore ? content.toLowerCase(Locale.ROOT) : content;
            int fromIndex = 0;
            int index;
            while ((index = searchContent.indexOf(searchKeyword, fromIndex)) != -1) {
                int start = Math.max(0, index - CONTEXT_SIZE);
                int end = Math.min(content.length(), index + originalKeyword.length() + CONTEXT_SIZE);
                String snippet = content.substring(start, end);
                String title = getParentDirectoryName(path);
                result.append("[").append(title).append("]\n")
                        .append(snippet).append("\n")
                        .append(path.toString()).append("\n\n");
                fromIndex = index + Math.max(searchKeyword.length(), CONTEXT_SIZE);
            }
        } catch (Exception ignored) {
            // Ignore file read errors for performance, continue searching other files
        }
    }

    private static String getParentDirectoryName(Path path) {
        Path parent = path.getParent();
        if (parent == null) {
            return "";
        }
        Path name = parent.getFileName();
        return name == null ? "" : name.toString();
    }
}
