package com.xiangzi.xiangziaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.xiangzi.xiangziaiagent.constants.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * PDF 生成工具类
 * 使用 iText7 库将指定文本内容生成为 PDF 文件并保存到本地。
 * 该工具通过 Spring AI 的 @Tool 注解注册为 AI 可调用的工具，支持中文字体渲染。
 */
public class PDFGenerationTool {

    /**
     * 根据给定内容生成 PDF 文件并保存到本地
     *
     * @param fileName 生成的 PDF 文件名（不含目录路径）
     * @param content  要写入 PDF 的文本内容（支持中文）
     * @return 生成结果提示信息（成功时返回保存路径，失败时返回错误信息）
     *
     * 实现流程：
     * 1. 基于 FileConstant.FILE_SAVE_DIR 常量拼接 PDF 保存目录（/pdf 子目录）
     * 2. 使用 Hutool 的 FileUtil.mkdir 确保目录存在
     * 3. 通过 try-with-resources 自动管理 PdfWriter、PdfDocument、Document 的资源
     * 4. 使用 iText7 内置的中文字体 "STSongStd-Light" 进行渲染（保证中文正常显示）
     * 5. 创建段落并写入文档
     * 6. 捕获 IO 异常并返回错误信息
     */
    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
        @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
        @ToolParam(description = "Content to be included in the PDF") String content) {
        // 构造 PDF 保存目录与完整文件路径：根目录 + /pdf + 文件名
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录（若已存在则自动跳过）
            FileUtil.mkdir(fileDir);
            // 使用 try-with-resources 依次创建 PdfWriter（底层写入）、PdfDocument（PDF 模型）、Document（高级 API）
            // 三者均实现了 AutoCloseable，退出块时会自动关闭并写入文件
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                // 自定义字体（需要人工下载字体文件到特定目录）
                //                String fontPath = Paths.get("src/main/resources/static/fonts/simsun.ttf")
                //                        .toAbsolutePath().toString();
                //                PdfFont font = PdfFontFactory.createFont(fontPath,
                //                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                // 使用 iText7 内置的中文字体 STSongStd-Light（UniGB-UCS2-H 为简体中文编码）
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);
                // 根据传入的内容创建 PDF 段落对象
                Paragraph paragraph = new Paragraph(content);
                // 将段落添加到文档中（try-with-resources 退出时会自动刷新并关闭文档）
                document.add(paragraph);
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            // 捕获 PDF 生成过程中的 IO 异常，返回友好的错误提示
            return "Error generating PDF: " + e.getMessage();
        }
    }
}