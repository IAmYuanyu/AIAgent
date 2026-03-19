package com.yuanyu.aiagent.demo.invoke;

import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MultiModalInvokeTest {
    @Autowired
    private MultiModalInvoke multiModalInvoke;

    @Test
    void simpleMultiModalConversationCall() {
        try {
            multiModalInvoke.simpleMultiModalConversationCall();
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        } catch (UploadFileException e) {
            throw new RuntimeException(e);
        }
    }
}