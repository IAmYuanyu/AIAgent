package com.yuanyu.aiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyManusTest {

    @Resource
    private MyManus myManus;

    @Test
    void agentTest() {
        String result = myManus.run("读取readTest文件的内容，将这个文件里的内容输出到一个PDF文件中");
        Assertions.assertNotNull(result);
    }

}