package com.yuanyu.aiagent.agent;

import com.yuanyu.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础代理类，用于管理智能体状态和执行流程
 *
 * 提供状态转换、记忆管理和基于步骤的执行循环的基础功能
 * 子类必须实现step方法
 */
@Data
@Slf4j
public abstract class BaseAgent {
    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextStepPrompt;

    // 状态
    private AgentState state = AgentState.IDLE;

    // 最大步数和当前步数（还可以设置个最大token限制）
    private int maxSteps = 10;
    private int currentStep = 0;

    // 大模型
    private ChatClient chatClient;

    // 对话记忆（自主维护对话上下文）
    private List<Message> messageList = new ArrayList<>();


    /**
     * 运行智能体
     * @param userPrompt 用户输入
     * @return
     */
    public String run(String userPrompt) {
        // 基础校验
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if (StringUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }

        // 执行
        // 设置状态为运行中
        state = AgentState.RUNNING;
        // 记录用户输入
        messageList.add(new UserMessage(userPrompt));
        // 执行循环
        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step " + stepNumber + "/" + maxSteps);
                // 单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ": \n" + stepResult;
                results.add(result);
            }
            // 检查是否超出限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 清理资源
            this.cleanup();
        }
    }

    /**
     * 定义执行步骤
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 可以让子类重写
    }
}
