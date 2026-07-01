package com.xiangzi.xiangziaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.xiangzi.xiangziaiagent.constants.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类
 * 提供基于 Spring AI Tool 框架的文件读写能力，
 * 允许 AI Agent 通过自然语言调用完成文件的读取与写入操作。
 */
public class FileOperationTool {

    /** 文件保存的根目录，由 FileConstant 中统一配置的目录 + "/file" 子目录组成 */
    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    /**
     * 读取指定文件的内容（以 UTF-8 编码读取）
     *
     * @param fileName 要读取的文件名称（包含扩展名，如 "data.txt"）
     * @return 文件的文本内容；读取失败时返回包含错误信息的字符串
     */
    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {
        // 拼接完整的文件路径
        String filePath = FILE_DIR + "/" + fileName;
        try {
            // 使用 Hutool 的 FileUtil 以 UTF-8 编码读取文件内容
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            // 捕获异常并返回友好的错误提示
            return "Error reading file: " + e.getMessage();
        }
    }

    /**
     * 将文本内容写入指定文件（以 UTF-8 编码写入）
     * 如果目标目录不存在，会自动创建目录后再写入。
     *
     * @param fileName 写入的目标文件名称（包含扩展名）
     * @param content  要写入文件的文本内容
     * @return 写入成功返回成功信息与文件路径；失败则返回包含错误信息的字符串
     */
    @Tool(description = "Write content to a file")
    public String writeFile(
        @ToolParam(description = "Name of the file to write") String fileName,
        @ToolParam(description = "Content to write to the file") String content) {
        // 拼接完整的文件路径
        String filePath = FILE_DIR + "/" + fileName;
        try {
            // 确保文件目录存在，若不存在则创建
            FileUtil.mkdir(FILE_DIR);
            // 使用 Hutool 的 FileUtil 以 UTF-8 编码写入内容到指定文件
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (Exception e) {
            // 捕获异常并返回友好的错误提示
            return "Error writing to file: " + e.getMessage();
        }
    }


}