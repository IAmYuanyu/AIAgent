package com.yuanyu.aiagent.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalOperationTool {

    @Tool(description = "在cmd执行命令")
    public String executeTerminalCommand(@ToolParam(description = "需要在终端执行的命令，比如要输出'yuanyu'，就传入echo 'yuanyu'") String command) {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("命令执行失败，退出代码为: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("命令执行失败: ").append(e.getMessage());
        }
        return output.toString();
    }
}