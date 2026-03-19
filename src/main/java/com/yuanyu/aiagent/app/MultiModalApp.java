package com.yuanyu.aiagent.app;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.yuanyu.aiagent.common.MultiModalFileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MultiModalApp {
    private final MultiModalConversation multiModalConversation;

    private final MultiModalConversationParam multiModalConversationParam;

    /**
     * 根据用户上传的文件和问题文本，动态构建多模态消息并调用 DashScope API
     *
     * @param files  用户上传的文件列表（只允许同一类型）
     * @param prompt 用户的文字问题
     * @return AI 回复的文本内容
     */
    public String multiModalConversationCall(List<MultipartFile> files, String prompt)
            throws NoApiKeyException, UploadFileException, IOException {

        // 1. 解析文件类型，校验不能混合上传
        MultiModalFileType unifiedType = resolveAndValidateFileType(files);

        // 2. 将上传的文件保存到临时目录，拼装 content 列表
        List<Map<String, Object>> contentList = new ArrayList<>();
        List<Path> tempFiles = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                // 创建临时文件
                Path tempPath = Files.createTempFile("multimodal_", "_" + file.getOriginalFilename());
                // 将上传的文件内容写入临时文件
                file.transferTo(tempPath.toFile());
                // 记录临时文件路径，方便finally块清理
                tempFiles.add(tempPath);
                // DashScope SDK 支持传入本地文件路径（file:// 协议）
                contentList.add(Collections.singletonMap(
                        unifiedType.getContentKey(),
                        tempPath.toUri().toString()
                ));
            }
            // 追加用户文字问题
            contentList.add(Collections.singletonMap("text", prompt));

            // 3. 构建消息并调用 API
            MultiModalMessage userMessage = MultiModalMessage.builder()
                    .role(Role.USER.getValue())
                    .content(contentList)
                    .build();

            // 设置消息到调用参数中（multiModalConversationParam是配置类中预先构建的参数对象）
            multiModalConversationParam.setMessages(List.of(userMessage));

            // 调用DashScope多模态API
            MultiModalConversationResult result = multiModalConversation.call(multiModalConversationParam);

            // 4. 提取文本回复
            return result.getOutput()
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent()
                    .stream()
                    .filter(m -> m.containsKey("text")) // 过滤出文字回复项
                    .map(m -> m.get("text").toString()) // 提取文字内容
                    .findFirst()
                    .orElse(""); // 无回复时返回空字符串
        } finally {
            // 5. 清理临时文件
            for (Path tempPath : tempFiles) {
                Files.deleteIfExists(tempPath);
            }
        }
    }

    /**
     * 解析并校验文件列表的类型一致性
     *
     * @param files 上传的文件列表
     * @return 统一的文件类型
     * @throws IllegalArgumentException 若文件列表为空、含不支持的类型、或包含多种类型
     */
    private MultiModalFileType resolveAndValidateFileType(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("请至少上传一个文件");
        }

        Set<MultiModalFileType> typeSet = new HashSet<>();
        for (MultipartFile file : files) {
            MultiModalFileType type = MultiModalFileType.fromFilename(file.getOriginalFilename());
            typeSet.add(type);
        }

        if (typeSet.size() > 1) {
            throw new IllegalArgumentException(
                    "不能同时上传不同类型的文件，检测到的类型：" + typeSet
            );
        }

        // 取出唯一的类型并返回
        return typeSet.iterator().next();
    }
}
