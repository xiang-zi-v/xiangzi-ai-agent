package com.xiangzi.xiangziaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.Scanner;

/**
 * 向人类提问的工具类
 * 允许 AI Agent 在遇到不确定或需要人类确认的问题时，
 * 通过控制台与人类用户交互，获取人类的输入后继续执行。
 */
public class AskHuman {

    private final Scanner scanner;

    public AskHuman() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * 向人类用户提出一个问题并等待其在控制台输入回答
     *
     * @param question 向用户提出的问题，例如 "请确认要删除该文件吗？(yes/no)"
     * @return 用户在控制台输入的回答内容；若读取输入失败则返回包含错误信息的字符串
     */
    @Tool(description = "Ask the human user a question and wait for their input from the console. " +
            "Use this tool when you need clarification, confirmation, or additional information " +
            "from a human user before proceeding with the task.")
    public String askHuman(
            @ToolParam(description = "The question to ask the human user, e.g., '请确认要删除该文件吗？(yes/no)'") String question) {
        try {
            System.out.println();
            System.out.println("=============== AI 向您提问 ===============");
            System.out.println("问题: " + question);
            System.out.print("请输入您的回答: ");

            StringBuilder responseBuilder = new StringBuilder();
            boolean firstLine = true;
            String line;

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    if (!firstLine) {
                        responseBuilder.append("\n");
                    }
                    responseBuilder.append(line);
                    firstLine = false;
                    break;
                }
            }

            String answer = responseBuilder.toString();
            System.out.println("============================================");
            System.out.println();

            return "用户的回答: " + answer;
        } catch (Exception e) {
            return "向用户提问时出错: " + e.getMessage();
        }
    }


}