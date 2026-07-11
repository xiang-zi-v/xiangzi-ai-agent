package com.xiangzi.xiangziaiagent.agent;

import com.xiangzi.xiangziaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;


@Component
public class XiangziManus extends ToolCallAgent {


    private final static String SYSTEM_PROMPT = """  
            You are XiangziManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
            You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
            """;

    private final static String NEXT_STEP_PROMPT = """  
            Based on user needs, proactively select the most appropriate tool or combination of tools.  
            For complex tasks, you can break down the problem and use different tools step by step to solve it.  
            After using each tool, clearly explain the execution results and suggest the next steps.  
            If you want to stop the interaction at any point, use the `terminate` tool/function call.  
            """;

    public XiangziManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);

        this.setName("XiangziManus");

        this.setSysPrompt(SYSTEM_PROMPT);

        this.setNextStepPrompt(NEXT_STEP_PROMPT);

        this.setMaxStep(20);

        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(
                        new MyLoggerAdvisor()
                )
                .build();

        this.setChatClient(chatClient);
    }


}
