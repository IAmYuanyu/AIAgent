package com.yuanyu.aiagent.demo.invoke;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring AI 调用
 */
@Component
@RequiredArgsConstructor
public class SpringAiAiInvoke implements CommandLineRunner {

    private final ChatModel dashscopeChatModel;

    // private final ChatClient chatClient;

    // 项目运行时自动调用
    @Override
    public void run(String... args) throws Exception {
        // AssistantMessage assistantMessage = dashscopeChatModel.call(new Prompt("Ciallo～(∠・ω< )⌒☆"))
        //         .getResult() // 获取结果
        //         .getOutput();// 获取输出
        // System.out.println(assistantMessage.getText());



        // ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
        //         .defaultSystem("用户说Ciallo时回复‘柚子厨蒸鹅心’")
        //         .build();
        //
        // String content = chatClient.prompt()
        //         .user("Ciallo～(∠・ω< )⌒☆")
        //         .call()
        //         .content();
        //
        // System.out.println(content);
    }
}
