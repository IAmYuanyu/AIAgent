package com.yuanyu.aiagent.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MarkdownSearchTool {

    private static final int CONTEXT_SIZE = 200;
    private static final String RESOURCE_ROOT = "static/post";

    @Tool(description = "根据关键词查询缘鱼博客文章，返回博客中匹配到关键词的部分，每个匹配到的部分所在文章标题用[]包裹")
    public String searchMarkdown(
            @ToolParam(description = "要搜索的关键词，每次只能传入一个关键词，不支持正则表达式") String keyword,
            @ToolParam(description = "是否忽略大小写，默认 true") Boolean ignoreCase) {
        if (keyword == null || keyword.isBlank()) {
            return "关键词不能为空";
        }
        boolean ignore = ignoreCase == null || ignoreCase;
        String searchKeyword = ignore ? keyword.toLowerCase(Locale.ROOT) : keyword;
        StringBuilder result = new StringBuilder();

        List<Resource> resources;
        try {
            resources = listMarkdownResources();
        } catch (IOException e) {
            return "搜索失败：" + e.getMessage();
        }

        if (resources.isEmpty()) {
            return "目录不存在或不可访问：" + RESOURCE_ROOT;
        }

        for (Resource resource : resources) {
            searchInResource(resource, searchKeyword, keyword, ignore, result);
        }

        if (result.length() == 0) {
            return "未找到匹配内容";
        }
        return result.toString();
    }

    private static List<Resource> listMarkdownResources() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<>();
        addResources(resources, resolver.getResources("classpath*:" + RESOURCE_ROOT + "/**/*.md"));
        addResources(resources, resolver.getResources("classpath*:" + RESOURCE_ROOT + "/**/*.markdown"));
        return resources;
    }

    private static void addResources(List<Resource> target, Resource[] resources) {
        if (resources == null) {
            return;
        }
        for (Resource resource : resources) {
            if (resource != null && resource.exists() && resource.isReadable()) {
                target.add(resource);
            }
        }
    }

    private static void searchInResource(Resource resource, String searchKeyword, String originalKeyword, boolean ignore, StringBuilder result) {
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);
            String searchContent = ignore ? content.toLowerCase(Locale.ROOT) : content;
            int fromIndex = 0;
            int index;
            while ((index = searchContent.indexOf(searchKeyword, fromIndex)) != -1) {
                int start = Math.max(0, index - CONTEXT_SIZE);
                int end = Math.min(content.length(), index + originalKeyword.length() + CONTEXT_SIZE);
                String snippet = content.substring(start, end);
                String title = getParentDirectoryName(resource);
                String path = getResourcePath(resource);
                result.append("[").append(title).append("]\n")
                        .append(snippet).append("\n")
                        .append(path).append("\n\n");
                fromIndex = index + Math.max(searchKeyword.length(), CONTEXT_SIZE);
            }
        } catch (Exception ignored) {
            // Ignore file read errors for performance, continue searching other files
        }
    }

    private static String getParentDirectoryName(Resource resource) {
        String relativePath = getRelativeResourcePath(resource);
        if (relativePath == null || relativePath.isBlank()) {
            return "";
        }
        int lastSlash = relativePath.lastIndexOf('/');
        if (lastSlash <= 0) {
            return "";
        }
        String parentPath = relativePath.substring(0, lastSlash);
        int parentSlash = parentPath.lastIndexOf('/');
        return parentSlash == -1 ? parentPath : parentPath.substring(parentSlash + 1);
    }

    private static String getResourcePath(Resource resource) {
        String relativePath = getRelativeResourcePath(resource);
        if (relativePath != null && !relativePath.isBlank()) {
            return RESOURCE_ROOT + "/" + relativePath;
        }
        return resource.getDescription();
    }

    private static String getRelativeResourcePath(Resource resource) {
        try {
            String url = resource.getURL().toString();
            String marker = RESOURCE_ROOT + "/";
            int index = url.indexOf(marker);
            if (index == -1) {
                return null;
            }
            return url.substring(index + marker.length());
        } catch (IOException e) {
            return null;
        }
    }
}
