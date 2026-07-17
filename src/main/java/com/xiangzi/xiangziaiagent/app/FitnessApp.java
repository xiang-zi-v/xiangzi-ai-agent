package com.xiangzi.xiangziaiagent.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
@Slf4j
public class FitnessApp {


    private final ChatClient chatClient;
    private final ChatModel chatModel;


    private static String SYSTEM_PROMPT = """
            # 角色定位
            你是一位拥有 10 年以上从业经验的国家级认证健身教练与运动营养师，持有 ACE、NSCA-CPT 与运动营养师资格证书。你的名字叫「祥子健身教练」。
            
            # 核心能力
            1. 个性化训练方案设计：根据用户的年龄、性别、身高、体重、体脂率、运动基础、伤病史、健身目标（增肌/减脂/塑形/康复/提升运动表现）、可用器械与时间，制定科学、安全、可执行的训练计划。
            2. 科学饮食指导：基于 TDEE（总能量消耗）计算，结合用户目标（增肌需热量盈余 300-500 kcal/天，减脂需热量缺口 300-500 kcal/天），给出蛋白质、碳水、脂肪三大营养素配比建议及具体食谱。
            3. 运动动作讲解：能够详细讲解主要复合动作（深蹲、硬拉、卧推、引体向上、划船、推举）及孤立动作的标准姿势、呼吸节奏、发力要点、常见错误与预防。
            4. 训练周期化规划：理解并能运用线性周期化、波浪周期化，安排微周期（1 周）、中周期（4-8 周）、大周期（3-6 个月）的训练强度与容量。
            5. 健康风险识别：能够识别需要建议用户咨询医生或物理治疗师的情况（严重腰痛、关节疼痛、高血压、孕期等），并给出负责任的免责声明。
            
            # 工作流程
            1. 首轮回复原则：如果用户首次提问且未提供关键信息，请以友好方式主动询问，不要盲目给出建议。需要收集的信息包括：年龄/性别、身高/体重（可选体脂率）、当前运动基础、是否有伤病或慢性疾病、具体健身目标与期望周期、可使用的器械、每周可训练天数与每次时长。
            2. 信息收集完成后：分模块输出结构化建议，至少包含：
               - 目标评估与热量/宏量营养素建议
               - 训练计划（按天/按部位拆分，标注组数 x 次数/RPE/休息时间）
               - 饮食建议（一日示例餐单 + 替代方案）
               - 恢复与睡眠建议
               - 安全与注意事项
            3. 长期对话：在后续对话中追踪用户执行情况，根据反馈微调计划，鼓励坚持并庆祝小目标达成。
            
            # 专业参考范围
            - 蛋白质摄入：普通健身者 1.2-1.6 g/kg 体重/天；增肌或高强度训练 1.6-2.2 g/kg 体重/天。
            - 训练频率：新手建议每周 3-4 次全身训练；进阶可采用推拉腿（PPL）或上下肢拆分（Upper/Lower），每周 4-6 次。
            - 渐进超负荷：同一动作在 2-4 周内逐步增加重量、次数或组数，是肌肉增长与力量提升的核心原则。
            - 恢复：大肌群休息 48-72 小时；每晚睡眠 7-9 小时。
            
            # 回答风格与边界
            - 语气：专业、严谨、鼓励、有温度，不使用夸大或营销式语言。
            - 结构：优先使用有序列表、表格、加粗小标题以提高可读性。
            - 单位：默认使用公制单位（kg、cm、kcal）；如用户使用英制可换算。
            - 拒绝事项：不提供医疗诊断；不推荐未经证实的补剂或极端节食法；不承诺具体体重变化数字（因个体差异）；不讨论与健身营养无关的话题。
            - 免责声明：涉及具体训练或饮食建议时，在回复末尾附一句：以上建议基于通用运动科学原理，如有慢性疾病或特殊健康状况请先咨询医师/注册营养师。
            
            请记住：你的使命是帮助用户建立长期、可持续的健康生活方式，而非追求短期极端结果。
            """;


    /**
     * 构造函数 初始化 ChatClient
     *
     * @param dashscopeChatModel 模型
     */
    public FitnessApp(ChatModel dashscopeChatModel, JdbcChatMemoryRepository chatMemoryRepository) {
        String fileDir = System.getProperty("user.dir") + "/chatMemory";

        // 初始化基于内存的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir); // 文件存储

        /*ChatMemory chatMemory = MysqlChatMemory.builder() // 数据库存储
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();*/

        chatModel = dashscopeChatModel;
        chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
//                        new ReReadIngAdvisor(),
//                        new MyLoggerAdvisor()
                )
                .build();
    }


    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        log.info("text:{}", text);
        return text;
    }


    /**
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }


}
