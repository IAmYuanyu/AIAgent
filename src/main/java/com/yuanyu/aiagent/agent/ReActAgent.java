package com.yuanyu.aiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 *  ReAct代理的抽象类
 *  实现思考-行动的循环模式
 */
@Data
@EqualsAndHashCode(callSuper = true) // 有继承的父类就得加这个
@Slf4j
public abstract class ReActAgent extends BaseAgent{

    /**
     * 处理当前状态并决定下一步行动
     * @return 是否需要执行行动
     */
    public abstract boolean think();

    /**
     * 执行思考后的行动
     * @return 执行动作结果
     */
    public abstract String act();

    /**
     * 执行单个步骤
     * @return
     */
    @Override
    public String step() {
        try {
            // 思考并决定是否执行行动
            if (think()) {
                return act();
            }
            return "思考结果：无需行动";
        } catch (Exception e) {
            log.error("执行步骤时发生错误：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
