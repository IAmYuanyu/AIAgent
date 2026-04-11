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

    @Test
    void doChatWithRAG() {
        String chatId = UUID.randomUUID().toString();
        // String message = "我有老婆了，但和老婆好像不怎么熟，该怎么办？有什么方法推荐吗？";
        String message = "2026年有什么电影推荐吗？";


        String string = loveApp.doChatWithRAG(message, chatId);
        Assertions.assertNotNull(string);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();

        // 测试高德地图Mcp服务
        // String message = "上海今天天气如何";

        // 测试自定义图片搜索Mcp服务
        // String message = "帮我搜索一些小狗的图片";

        // 测试我自己写的github仓库阅读mcp服务
        String message = "帮我看看这个仓库：https://github.com/IAmYuanyu/GitDemo，中的README文件的内容是什么";

        String result = loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(result);
    }
}