package com.yuanyu.aiagent.agent;


import cn.hutool.core.collection.CollUtil;
import com.yuanyu.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类
 * 具体实现了think和act方法
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ToolCallAgent extends ReActAgent{

    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存工具调用信息的响应结果
    private ChatResponse toolCallResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 用于定义选项属性，禁用SpringAI内置的工具调用机制，自己手动维护选项和上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用SpringAI内置的工具调用机制，自己手动维护选项和上下文
        this.chatOptions = ToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false) // TODO 与教程不一致，可能出bug，导致无法调用工具
                .build();
    }

    /**
     * 思考当前问题并决定是否调用工具
     * @return true表示需要调用工具，false表示不需要调用工具
     */
    @Override
    public boolean think() {
        // 校验是否有下一步的提示词
        if (!StringUtil.isBlank(getNextStepPrompt())) {
            // 若有，则拼接提示词到上下文中
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }

        // 调用大模型，获取工具调用结果
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        try {
            // 记录响应，用于后续Act
            this.toolCallResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();
        } catch (Exception e) {
            log.error("{}的思考过程遇到了问题：{}", getName(), e.getMessage());
            getMessageList().add(new AssistantMessage("思考问题时遇到错误：" + e.getMessage()));
            return false;
        }

        // 解析工具调用结果，获取要调用的工具
        // 获取助手消息（AI的回答内容）
        AssistantMessage assistantMessage = toolCallResponse.getResult().getOutput();
        // 获取要调用的工具列表
        List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
        // 输出提示信息
        String result = assistantMessage.getText();
        log.info("{}的思考结果为：{}", getName(), result);
        log.info("{}选择了{}个工具", getName(), toolCallList.size());
        // 判断是否需要调用工具（要调用的工具列表是否为空）
        if (toolCallList.isEmpty()) {
            // 继续输出提示信息
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info("{}的选中工具信息为：{}", getName(), toolCallInfo);

            // 若不需要调用则把助手消息添加到上下文
            getMessageList().add(assistantMessage);
            return false;
        } else {
            // 若需要调用工具，则不添加到上下文
            // 因为调用工具时会记录，所以这里不需要添加
            return true;
        }
    }

    /**
     * 执行工具调用
     * @return 执行结果
     */
    @Override
    public String act() {
        if (!toolCallResponse.hasToolCalls()) {
            // 虽然一般情况下，没有工具需要调用不会执行到这个逻辑，但是为了避免异常，这里加上判断
            return "没有工具需要调用";
        }
        // 手动调用工具
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallResponse);
        // 记录上下文，conversationHistory 包含了助手消息和工具调用结果
        setMessageList(toolExecutionResult.conversationHistory());
        // 获取上下文中最新消息
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        // 判断是否调用了任务终止工具
        boolean isDoTerminate = toolResponseMessage.getResponses().stream()
                .anyMatch(toolResponse -> toolResponse.name().equals("doTerminate"));
        if (isDoTerminate) {
            // 若调用了任务终止工具，则更改状态
            setState(AgentState.FINISHED);
        }
        // 输出调用结果
        String result = toolResponseMessage.getResponses().stream()
                .map(toolResponse -> "工具" + toolResponse.name() + "的调用结果：" + toolResponse.responseData())
                .collect(Collectors.joining("\n"));
        log.info("{}的工具调用结果：\n{}", getName(), result);

        return result;
    }
}
