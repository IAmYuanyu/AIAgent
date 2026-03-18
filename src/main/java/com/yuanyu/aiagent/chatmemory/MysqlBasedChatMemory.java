package com.yuanyu.aiagent.chatmemory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanyu.aiagent.entity.ChatMessageEntity;
import com.yuanyu.aiagent.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MysqlBasedChatMemory implements ChatMemory {

    private final ChatMessageMapper chatMessageMapper;

    @Override
    public void add(String conversationId, Message message) {
        ChatMemory.super.add(conversationId, message);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setConversationId(conversationId);
            entity.setMessageType(message.getMessageType().getValue());
            entity.setContent(message.getText());
            entity.setCreateTime(LocalDateTime.now());
            chatMessageMapper.insert(entity);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        LambdaQueryWrapper<ChatMessageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageEntity::getConversationId, conversationId)
                .orderByAsc(ChatMessageEntity::getCreateTime);

        List<ChatMessageEntity> entities = chatMessageMapper.selectList(queryWrapper);
        List<Message> messages = new ArrayList<>();

        for (ChatMessageEntity entity : entities) {
            Message message = convertToMessage(entity);
            if (message != null) {
                messages.add(message);
            }
        }

        return messages;
    }

    @Override
    public void clear(String conversationId) {
        LambdaQueryWrapper<ChatMessageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageEntity::getConversationId, conversationId);
        chatMessageMapper.delete(queryWrapper);
    }

    /**
     * 将数据库实体转换为Message对象
     */
    private Message convertToMessage(ChatMessageEntity entity) {
        String messageType = entity.getMessageType();
        String content = entity.getContent();

        return switch (messageType) {
            case "user" -> new UserMessage(content);
            case "assistant" -> new AssistantMessage(content);
            case "system" -> new SystemMessage(content);
            default -> null;
        };
    }
}