package com.yuanyu.aiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

public class LoveAppContextualQueryAugmenterFactory {
    public static ContextualQueryAugmenter createInstance() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                【强制指令：忽略之前的角色设定】
                由于检索不到任何参考上下文，请停止扮演恋爱专家。
                严禁输出任何建议或开场白。
                你只需输出以下指定内容，不要包含任何其他字符：
                抱歉，我不知道！诶嘿⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄
                """);

        return ContextualQueryAugmenter.builder()
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .allowEmptyContext(false)
                .build();
    }
}
