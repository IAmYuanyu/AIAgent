package com.yuanyu.aiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MultiQueryExpanderInvokeTest {

    @Resource
    private MultiQueryExpanderInvoke multiQueryExpanderInvoke;

    @Test
    void queryExpand() {
        List<Query> queryList = multiQueryExpanderInvoke.queryExpand("RAG是啥玩意");
        assertNotNull(queryList);
    }
}