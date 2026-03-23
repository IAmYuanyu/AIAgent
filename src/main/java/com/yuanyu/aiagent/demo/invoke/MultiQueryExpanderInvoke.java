package com.yuanyu.aiagent.demo.invoke;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询扩展器
 */
@Component
public class MultiQueryExpanderInvoke {

    private final ChatClient.Builder chatClientBuilder;

    public MultiQueryExpanderInvoke(ChatModel dashscopeChatModel) {
        this.chatClientBuilder = ChatClient.builder(dashscopeChatModel);
    }

    public List<Query> queryExpand(String query) {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3)
                .build();
        return queryExpander.expand(new Query(query));
    }
}
