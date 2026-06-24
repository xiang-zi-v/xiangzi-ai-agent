package com.xiangzi.xiangziaiagent.etl;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 恋爱应用上下文查询增强器工厂类
 *
 * 该工厂类负责创建针对恋爱应用场景的ContextualQueryAugmenter实例，
 * 用于在RAG（检索增强生成）流程中处理查询上下文的增强。
 *
 * @author
 * @since
 */
public class LoveAppContextualQueryAugmenterFactory {

    /**
     * 创建恋爱应用专用的上下文查询增强器实例
     *
     * 该方法创建的增强器具有以下特性：
     * - 不允许空上下文（allowEmptyContext = false）
     * - 当上下文为空时，返回预设的提示信息，引导用户提问恋爱相关问题
     *
     * @return ContextualQueryAugmenter 配置好的上下文查询增强器实例
     */
    public static ContextualQueryAugmenter createInstance() {
        // 创建空上下文时的提示模板，提示用户只能回答恋爱相关问题
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                                                                       你应该输出下面的内容：
                                                                       抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                                                                       有问题可以联系编程导航客服 https://codefather.cn
                                                                       """);

        // 构建并返回ContextualQueryAugmenter实例
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)              // 设置不允许空上下文
                .emptyContextPromptTemplate(emptyContextPromptTemplate)  // 设置空上下文时的提示模板
                .build();
    }
}
