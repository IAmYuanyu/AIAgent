package com.yuanyu.aiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;

public class LangChainAiInvoke {
    public static void main(String[] args) {

        ChatModel qwenModel = QwenChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("qwen-max")
                .build();

        String result = qwenModel.chat("Ciallo～(∠・ω< )⌒☆");
        System.out.println(result);
    }
}
