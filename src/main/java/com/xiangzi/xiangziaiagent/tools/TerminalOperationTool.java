package com.xiangzi.xiangziaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具类
 * 用于在终端（命令行）中执行系统命令，并返回命令的输出结果。
 * 该工具通过 Spring AI 的 @Tool 注解注册为 AI 可调用的工具。
 */
public class TerminalOperationTool {

    /**
     * 在终端中执行指定的命令并返回执行结果
     *
     * @param command 要在终端中执行的命令字符串
     * @return 命令执行的输出结果（包括标准输出和错误信息）
     *
     * 实现流程：
     * 1. 使用 ProcessBuilder 构造 Windows cmd.exe 进程，通过 /c 参数执行传入的命令
     * 2. 启动进程并读取其标准输出流，逐行收集输出内容
     * 3. 等待命令执行完成，检查退出码；非 0 表示执行失败，追加失败信息
     * 4. 捕获 IO 异常和中断异常，将错误信息追加到输出中
     */
    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        StringBuilder output = new StringBuilder();
        try {
            // 使用 ProcessBuilder 构造 cmd.exe 进程，以 /c 参数方式执行命令（更安全可控）
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            //            Process process = Runtime.getRuntime().exec(command);
            Process process = builder.start();
            // 使用 try-with-resources 自动关闭输入流，逐行读取进程的标准输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            // 等待进程执行完成并获取退出码；退出码非 0 表示命令执行失败
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            // 捕获命令执行过程中的 IO 异常和中断异常，将错误信息追加到输出结果
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}