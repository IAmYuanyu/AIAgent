package com.yuanyu.aiagent.controller;

import com.yuanyu.aiagent.agent.MyManus;
import com.yuanyu.aiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] toolCallbacks;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用LoveApp
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String chatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }


    /**
     * 流式调用LoveApp
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatWithLoveAppStream(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * 流式使用Manus
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        MyManus myManus = new MyManus(toolCallbacks, dashscopeChatModel);
        return myManus.runByStream(message);
    }
}
