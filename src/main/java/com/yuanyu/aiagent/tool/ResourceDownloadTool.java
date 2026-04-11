package com.yuanyu.aiagent.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.yuanyu.aiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

public class ResourceDownloadTool {

    @Tool(description = "通过给出的url来下载资源")
    public String downloadResource(@ToolParam(description = "需要下载的资源的url") String url,
                                   @ToolParam(description = "下载的资源需要保存为的名称") String fileName) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/download"; // 文件保存的目录
        String filePath = fileDir + "/" + fileName;
        try {
            
            FileUtil.mkdir(fileDir);
            
            HttpUtil.downloadFile(url, new File(filePath));
            return "资源下载成功: " + filePath;
        } catch (Exception e) {
            return "资源下载失败: " + e.getMessage();
        }
    }
}