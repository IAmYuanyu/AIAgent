package com.yuanyu.aiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void doChat() {
        // 随机生成一个会话 ID
        String chatId = UUID.randomUUID().toString();

        // 测试对话
        // // 第一轮
        // String message = "你好，我是缘鱼。";
        // String answer = loveApp.doChat(message, chatId);
        //
        // // 第二轮
        // message = "Ciallo～(∠・ω< )⌒☆";
        // answer = loveApp.doChat(message, chatId);
        // Assertions.assertNotNull(answer); // 断言返回结果不为空，若为空则测试失败
        //
        // // 第三轮
        // message = "我是谁？";
        // answer = loveApp.doChat(message, chatId);
        // Assertions.assertNotNull(answer);

        // loveApp.doChat("希望你妈身体健康", chatId);
        // loveApp.doChat("Ciallo～(∠・ω< )⌒☆，我叫缘鱼，Ciallo是打招呼的意思。", chatId);
        // loveApp.doChat("我是谁？Ciallo是什么意思？", chatId);
        loveApp.doChat("你是谁？", chatId);

    }

    @Test
    void doChatWithReport() {
        // 随机生成一个会话 ID
        String chatId = UUID.randomUUID().toString();

        String message = "我是一个肥宅，我每天躺在家里懒得出门，长得也丑，我也不愿做出任何改变，我要如何找到女朋友？";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }
}