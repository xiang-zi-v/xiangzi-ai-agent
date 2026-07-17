package com.xiangzi.xiangziaiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类
 * 实现了思考-行动的循环模式
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReActAgent extends BaseAgent {

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动，true表示需要执行，false表示不需要执行
     */
    public abstract boolean think(SseEmitter emitter);

    /**
     * 执行决定的行动
     *
     * @return 行动执行结果
     */
    public abstract String act();


    /**
     * 执行单个步骤：思考和行动
     *
     * @return 步骤执行结果
     */
    @Override
    public String step(SseEmitter emitter) {
        // 执行父类的每一个步骤，每个步骤分为思考和行动
        try {
            boolean thinkResult = this.think(emitter);
            if (!thinkResult) {
                return "思考完成 - 无需行动";
            }
            return this.act();
        } catch (Exception e) {
            return "步骤执行失败：" + e.getMessage();
        }
    }


}
