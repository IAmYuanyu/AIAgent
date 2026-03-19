package com.yuanyu.aiagent.common;

import java.util.Set;

/**
 * 多模态文件类型枚举，与 DashScope MultiModalMessage 的 content key 对应
 */
public enum MultiModalFileType {

    IMAGE("image", Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff")),
    VIDEO("video", Set.of("mp4", "avi", "mov", "mkv", "flv", "wmv", "webm")),
    AUDIO("audio", Set.of("mp3", "wav", "aac", "ogg", "flac", "m4a", "amr"));

    /** 对应 DashScope MultiModalMessage content 中的 key */
    private final String contentKey;
    /** 该类型支持的扩展名集合（小写） */
    private final Set<String> extensions;

    MultiModalFileType(String contentKey, Set<String> extensions) {
        this.contentKey = contentKey;
        this.extensions = extensions;
    }

    public String getContentKey() {
        return contentKey;
    }

    /**
     * 根据文件扩展名解析文件类型
     *
     * @param filename 文件名（含扩展名）
     * @return 对应的 MultiModalFileType
     * @throws IllegalArgumentException 若扩展名不支持
     */
    public static MultiModalFileType fromFilename(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("无法识别文件类型，文件名：" + filename);
        }
        // 提取并标准化扩展名（转小写）
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        // 遍历枚举常量，匹配扩展名
        for (MultiModalFileType type : values()) {
            if (type.extensions.contains(ext)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的文件类型：." + ext);
    }
}