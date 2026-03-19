package com.yuanyu.aiagent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class DemoController {

    private final ChatClient chatClient;

    public DemoController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/generation")
    String generation(String userInput) {
        return this.chatClient.prompt()
                .system("用户说Ciallo时请回复‘柚子厨蒸鹅心’")
                .user(userInput)
                .call()
                .content();
    }

    @GetMapping("/check")
    public String check() {
        return "OK";
    }


}
