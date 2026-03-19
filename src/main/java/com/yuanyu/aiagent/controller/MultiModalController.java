package com.yuanyu.aiagent.controller;

import com.yuanyu.aiagent.app.MultiModalApp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/multimodal")
@RequiredArgsConstructor
public class MultiModalController {

    private final MultiModalApp multiModalApp;

    /**
     * 多模态对话接口
     * <p>
     * 请求示例（form-data）：
     * - files: 上传的文件（可多个，但必须同一类型：全图片 or 全视频 or 全音频）
     * - prompt: 用户的问题文本
     *
     * @param files  上传的文件列表
     * @param prompt 用户的问题
     * @return AI 回复
     */
    @PostMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("prompt") String prompt) {
        try {
            String answer = multiModalApp.multiModalConversationCall(files, prompt);
            return ResponseEntity.ok(answer);
        } catch (IllegalArgumentException e) {
            // 文件类型错误（不支持的类型 / 混合类型）
            log.warn("文件类型校验失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("多模态调用异常", e);
            return ResponseEntity.internalServerError().body("调用 AI 服务失败：" + e.getMessage());
        }
    }
}