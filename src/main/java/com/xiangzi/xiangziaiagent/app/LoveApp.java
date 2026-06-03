package com.xiangzi.xiangziaiagent.app;

import com.xiangzi.xiangziaiagent.advisor.MyLoggerAdvisor;
import com.xiangzi.xiangziaiagent.chatmemory.FileBasedChatMemory;
import com.xiangzi.xiangziaiagent.chatmemory.MysqlChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;
    private final ChatModel chatModel;


    private static String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";


    /**
     * 构造函数 初始化 ChatClient
     *
     * @param dashscopeChatModel 模型
     */
    public LoveApp(ChatModel dashscopeChatModel, JdbcChatMemoryRepository chatMemoryRepository) {
        String fileDir = System.getProperty("user.dir") + "/chatMemory";

        ChatMemory chatMemory = new InMemoryChatMemory(); // 内存存储
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir); // 文件存储

        /*ChatMemory chatMemory = MysqlChatMemory.builder() // 数据库存储
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();*/

        chatModel = dashscopeChatModel;
        chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
//                        new ReReadIngAdvisor(),
//                        new MyLoggerAdvisor()
                )
                .build();
    }


    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                )
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        log.info("text:{}", text);
        return text;
    }


    public void coChatConverter(String userInput) {
        LoveReport actorsFilms = ChatClient.create(chatModel).prompt()
                .user(userInput)
                .call()
                .entity(LoveReport.class);

        log.info("actorsFilms:{}", actorsFilms);
    }

    record LoveReport(String title, List<String> suggestions) {
    }


    /**
     * 结构化输出恋爱报告
     *
     * @param message 用户消息
     * @param chatId  会话id
     * @return 恋爱报告
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient.prompt()
                .system(SYSTEM_PROMPT + " 每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                )
                .call()
                .entity(LoveReport.class);

        log.info("loveReport:{}", loveReport);
        return loveReport;
    }


    public String doChatUploadFile(String message, String chatId) throws IOException {
        String response = ChatClient.create(chatModel)
                .prompt()

                .user(u -> {
                    try {
                        u.text("图中描绘的是什么景象??")
                                .media(MimeTypeUtils.IMAGE_PNG, new FileUrlResource("D:\\Users\\xiangzhi\\Desktop\\images\\e5c79f8c5cbc820647e41dd1ec315ade.jpg"));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })

                .call()
                .content();
        log.info("response:{}", response);
        return response;
    }

    @Resource
    private VectorStore loveAppVectorStore;
    public String doChatWithVectorStore(String prompt, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(prompt)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                )
                .advisors(new MyLoggerAdvisor())
                .advisors(
                        new QuestionAnswerAdvisor(loveAppVectorStore)
                )
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        log.info("text:{}", text);
        return text;
    }


}
