//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.yuanyu.aiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

@Slf4j
public class PoliteCheckAdvisor implements CallAdvisor, StreamAdvisor {


    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        if (!politeCheck(chatClientRequest)) {
            throw new RuntimeException("请勿使用敏感词");
        }
        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        if (!politeCheck(chatClientRequest)) {
            throw new RuntimeException("请勿使用敏感词");
        }
        return streamAdvisorChain.nextStream(chatClientRequest);
    }

    public boolean politeCheck(ChatClientRequest chatClientRequest) {
        String userPrompt = chatClientRequest.prompt().getUserMessage().getText();
        if (userPrompt.contains("你妈")) {
            log.info("用户输入了敏感词");
            return false;
        }
        return true;
    }


    @Override
    public String getName() {
        return "文明用语好帮手";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
