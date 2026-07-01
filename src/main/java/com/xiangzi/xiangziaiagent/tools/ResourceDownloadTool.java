package com.xiangzi.xiangziaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.xiangzi.xiangziaiagent.constants.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * 资源下载工具类
 * 用于从指定 URL 下载资源并保存到本地文件系统。
 * 该工具通过 Spring AI 的 @Tool 注解注册为 AI 可调用的工具，内部使用 Hutool 工具库处理文件操作和 HTTP 下载。
 */
public class ResourceDownloadTool {

    /**
     * 从指定 URL 下载资源并保存到本地文件
     *
     * @param url      要下载的资源的 URL 地址
     * @param fileName 保存到本地的文件名称（不含目录路径）
     * @return 下载结果提示信息（成功时返回保存路径，失败时返回错误信息）
     *
     * 实现流程：
     * 1. 基于 FileConstant.FILE_SAVE_DIR 常量拼接下载目录路径（/download 子目录）
     * 2. 构造完整的文件保存路径
     * 3. 使用 Hutool 的 FileUtil.mkdir 确保目录存在
     * 4. 使用 Hutool 的 HttpUtil.downloadFile 执行 HTTP 下载并写入本地文件
     * 5. 捕获异常并返回友好的错误信息
     */
    @Tool(description = "Download a resource from a given URL")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url, @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
        // 构造下载目录与完整文件路径：根目录 + /download + 文件名
        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录（若已存在则自动跳过）
            FileUtil.mkdir(fileDir);
            // 使用 Hutool 的 downloadFile 方法下载资源到指定文件
            HttpUtil.downloadFile(url, new File(filePath));
            return "Resource downloaded successfully to: " + filePath;
        } catch (Exception e) {
            // 捕获下载过程中的所有异常（如网络异常、IO 异常等），返回错误提示
            return "Error downloading resource: " + e.getMessage();
        }
    }
}