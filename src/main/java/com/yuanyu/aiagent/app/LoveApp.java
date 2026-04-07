package com.yuanyu.aiagent.app;

import com.yuanyu.aiagent.advisor.MyLoggerAdvisor;
import com.yuanyu.aiagent.advisor.PoliteCheckAdvisor;
import com.yuanyu.aiagent.advisor.ReReadingAdvisor;
import com.yuanyu.aiagent.chatmemory.FileBasedChatMemory;
import com.yuanyu.aiagent.chatmemory.MysqlBasedChatMemory;
import com.yuanyu.aiagent.config.ToolRegistrationConfig;
import com.yuanyu.aiagent.rag.LoveAppDocumentLoader;
import com.yuanyu.aiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.yuanyu.aiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LoveApp {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。" +
            "开场向用户表明身份，告知用户可倾诉恋爱难题。围绕单身、恋爱、已婚三种状态提问：" +
            "单身状态询问社交圈拓展及追求心仪对象的困扰；恋爱状态询问沟通、习惯差异引发的矛盾；" +
            "已婚状态询问家庭责任与亲属关系处理的问题。引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    @Resource
    private VectorStore loveAppVectorStore;

    // @Resource
    // private VectorStore pgVectorVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private QueryRewriter queryRewriter;

    @Resource
    private ToolCallback[] allTools;

    /**
     * 初始化 ChatClient，基于Mysql持久化对话
     * @param dashscopeChatModel
     */
    public LoveApp(MysqlBasedChatMemory memory,
                   ChatModel dashscopeChatModel
                   // 从类路径资源加载系统提示模板，引入Spring的Value注解和Resource，别导错了
                   // @Value("classpath:/templates/prompts/Cat.md") Resource systemResource
    ) {
        // 初始化基于内存的对话记忆
        // ChatMemory memory = MessageWindowChatMemory.builder()
        //         .maxMessages(10) // 最多保存 10 条消息（默认20条）
        //         .build();

        // 初始化基于本地文件的对话记忆
        // String fileDir = System.getProperty("user.dir") + "/tmp/char-memory";
        // FileBasedChatMemory memory = new FileBasedChatMemory(fileDir);

        // SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
        // Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "耄耋"));
        // Prompt systemPrompt = new Prompt(systemMessage);


        chatClient = ChatClient.builder(dashscopeChatModel)
                // .defaultSystem(systemPrompt.getContents())
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build()
                        // new PoliteCheckAdvisor() // 文明卫士
                        // new MyLoggerAdvisor() // 自定义日志拦截器
                        // new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * AI 恋爱对话
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .system("简短地回答") // 测试用
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    // 定‌义恋爱报告类，可以使用 Java 14 引入的 record 特性快速定义
    record LoveReport(String title, List<String> suggestions) {
    }

    /**
     * AI 恋爱报告 - 结构化输出练习
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient.prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }


    /**
     * 使用 RAG 进行内容检索
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRAG(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message) // 使用用户原本的提示词
                // .user(queryRewriter.doQueryRewrite(message)) // 使用查询重写后的提示词
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                // 使用 RAG 知识库问答
                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                // 基于云知识库服务，使用 RAG 检索增强服务
                // .advisors(loveAppRagCloudAdvisor)
                // 基于 PgVectorVectorStore 向量存储，使用 RAG 检索增强服务
                // .advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())
                // 自定义检索过滤条件
                // .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppVectorStore, "已婚"))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * 使用工具进行增强的对话
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .system("简短地回答") // 测试用
                .tools(allTools)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor()) // 开启日志拦截器，方便观察
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
