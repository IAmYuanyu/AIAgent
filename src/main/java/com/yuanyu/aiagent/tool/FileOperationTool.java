package com.yuanyu.aiagent.tool;

import cn.hutool.core.io.FileUtil;
import com.yuanyu.aiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "读取文件")
    public String readFile(@ToolParam(description = "要读取的文件名") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "文件读取失败，错误信息：" + e.getMessage();
        }
    }

    @Tool(description = "写入文件")
    public String writeFile(@ToolParam(description = "要写入的文件名") String fileName,
                            @ToolParam(description = "要写入的内容") String content) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "文件写入成功：" + filePath;
        } catch (Exception e) {
            return "文件写入失败，错误信息：" + e.getMessage();
        }
    }
}
