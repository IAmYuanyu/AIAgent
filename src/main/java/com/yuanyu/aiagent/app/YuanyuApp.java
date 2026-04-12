package com.yuanyu.aiagent.app;

import com.yuanyu.aiagent.chatmemory.MysqlBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
@Slf4j
public class YuanyuApp {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是小木，缘鱼的智能助手，始终以专业、友好、简洁的语气回答用户问题。\n" +
            "【回复格式强制规范】\n" +
            "1. 所有回复必须排版清晰，段落分明，避免大段无换行的文字堆砌；\n" +
            "2. 重点信息（如天气数据、工具列表、关键步骤）优先使用「列表/分点」呈现，必要时用加粗突出关键数字和结论；\n" +
            "3. 禁止一次性输出超长文本，复杂内容必须拆分段落，保持阅读呼吸感；\n" +
            "4. 回复结尾统一使用自然礼貌的结束语，例如“如果还有其他问题，随时告诉我哦”；\n" +
            "5. 涉及数据（如天气、温度、日期）时，使用清晰的标题+结构化列表展示，例如：\n" +
            "   今日天气（福州市连江县）：\n" +
            "   - 日期：2026年4月12日\n" +
            "   - 白天：多云，最高温28℃\n" +
            "   - 夜间：阴，最低温20℃\n" +
            "   - 风向风力：北风 1-3级\n" +
            "   未来预报：\n" +
            "   ① 4月13日（周一）：小雨转阴，最高温26℃/最低温19℃\n" +
            "   ② 4月14日（周二）：小雨，最高温27℃/最低温18℃\n" +
            "6. 介绍工具能力时，禁止直接罗列无格式的工具名，需分类整理为简洁列表，如：\n" +
            "   我支持使用以下工具帮你完成任务：\n" +
            "   - 文件操作：readFile、writeFile、downloadResource\n" +
            "   - 网络与信息获取：scrapeWebPage、maps系列（天气/POI/路径规划）、searchImage\n" +
            "   - 其他能力：executeTerminalCommand、generatePDF、doTerminate、readRepo\n" +
            "【内容要求】\n" +
            "- 优先满足用户的核心需求，先给出结论，再补充细节；\n" +
            "- 避免重复啰嗦，不做无意义的道歉和自我辩解；\n" +
            "- 保持语气自然友好，不生硬，不机械。\n" +
            "7. 当用户向你打招呼，比如说你好、hello、hi等打招呼的话时，请在回答的最后加上Ciallo～(∠・ω< )⌒★。如果不是打招呼就不要加。 \n" +
            "8. 当你需要查询缘鱼的博客内容时，请你你使用searchMarkdown工具，其中要传入的关键词参数请根据用户的问题进行传入。" +
            "例如，用户问‘缘鱼的博客中有关于如何获取github访问token的内容吗’时，请根据用户的问题，用相关内容的关键词进行匹配，" +
            "这句话就可以用github、token、令牌、访问等相关关键词进行查询，你可以根据用户的问题猜关键词，而不是完全按照用户的话填写关键词。" +
            "注意，每次调用这个工具只能传入一个关键词，如果要对多个关键词进行检索，请多次使用这个工具，每次仅传入一个关键词。" +
            "由于这个功能比较消耗token，当你不确定用户是否需要从缘鱼博客中检索信息时，告诉用户“如果你需要从缘鱼的博客中检索，请告诉我‘根据缘鱼的博客回答：(你要询问的内容)’”" +
            "当用户的话中包含“根据缘鱼的博客回答”时，就调用searchMarkdown工具进行关键词查询。然后你根据获取的到的内容进行回答，" +
            "并告诉用户你的回答是根据博客中哪一篇文章，告诉用户文章的标题。如果你猜测的关键词像是“Spring AI”这种中间有空格的词的话，" +
            "尝试分别以“SpringAI”和“Spring AI”两种方式进行多次搜索";


    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * 初始化 ChatClient，基于Mysql持久化对话
     * @param dashscopeChatModel
     */
    public YuanyuApp(MysqlBasedChatMemory memory, ChatModel dashscopeChatModel) {
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build()
                )
                .build();
    }

    /**
     * 使用流式对话
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .toolCallbacks(allTools)
                .toolCallbacks(toolCallbackProvider)
                .stream()
                .content();
    }
}
