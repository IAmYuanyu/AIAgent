package com.yuanyu.aiagent.demo.invoke;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 使用纯 Hutool 工具包调用阿里云通义千问 API
 * 仅依赖：hutool-all
 */
public class HttpAiInvoke {
    // 替换为你的 DashScope API Key
    private static final String DASHSCOPE_API_KEY = System.getenv("DASHSCOPE_API_KEY");
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public static void main(String[] args) {
        try {
            // 1. 构建请求体 JSON（使用 Hutool 自带的 JSON 工具）
            JSONObject requestBody = buildRequestBody();

            // 2. 发送 POST 请求
            String response = sendPostRequest(API_URL, requestBody.toString());

            // 3. 处理响应结果
            System.out.println("API 响应结果：");
            System.out.println(response);

        } catch (Exception e) {
            System.err.println("请求失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 构建请求体 JSON 结构（仅使用 Hutool 的 JSON 类）
     */
    private static JSONObject buildRequestBody() {
        // 外层对象
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen-plus");

        // input 部分
        JSONObject input = new JSONObject();
        // messages 数组（使用 Hutool 的 JSONArray）
        JSONArray messages = new JSONArray();

        // system 消息
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are a helpful assistant.");
        messages.add(systemMsg);

        // user 消息
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", "你是谁？");
        messages.add(userMsg);

        input.put("messages", messages);
        requestBody.put("input", input);

        // parameters 部分
        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message");
        requestBody.put("parameters", parameters);

        return requestBody;
    }

    /**
     * 使用 Hutool 发送 POST 请求
     * @param url 请求地址
     * @param jsonBody 请求体 JSON 字符串
     * @return 响应结果字符串
     */
    private static String sendPostRequest(String url, String jsonBody) {
        // 构建 HTTP POST 请求（try-with-resources 自动关闭响应）
        try (HttpResponse response = HttpRequest.post(url)
                // 设置认证头（和原 curl 一致）
                .header(Header.AUTHORIZATION, "Bearer " + DASHSCOPE_API_KEY)
                // 设置 Content-Type 为 JSON
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                // 设置请求体（指定 UTF-8 编码）
                .body(jsonBody)
                // 设置超时时间（10 秒，可调整）
                .timeout(10000)
                // 执行请求
                .execute()) {

            // 检查响应状态码
            if (response.isOk()) {
                return response.body();
            } else {
                throw new RuntimeException("请求失败，状态码：" + response.getStatus()
                        + "，响应内容：" + response.body());
            }
        }
    }
}
