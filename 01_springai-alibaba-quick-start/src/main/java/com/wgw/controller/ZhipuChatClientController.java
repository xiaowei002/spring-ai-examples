package com.wgw.controller;

import com.wgw.advisor.SGAdvisor1;
import com.wgw.advisor.SGAdvisor2;
import com.wgw.advisor.SimpleMessageChatMemoryAdvisor;
import com.wgw.model.Book;
import org.apache.logging.log4j.util.Strings;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chatClient")
public class ZhipuChatClientController {

    private final ChatClient chatClient;

    public ZhipuChatClientController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }
//
//    @GetMapping("/simple")
//    public String simple(@RequestParam(name = "query") String query) {
//        ZhiPuAiChatOptions chatOptions = new ZhiPuAiChatOptions();
//        chatOptions.setModel("glm-4.6"); //设置模型
//        SystemMessage systemMessage = new SystemMessage("你是一个ai助手");
//        UserMessage userMessage = new UserMessage(query);
//        //构建prompt
//        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), chatOptions);
//        return chatClient.prompt(prompt)
//                .call()
//                .content();
//    }

    @GetMapping("/simple")
    public String simple(@RequestParam(name = "query") String query) {
        ZhiPuAiChatOptions chatOptions = ZhiPuAiChatOptions.builder()
                .model("glm-4.6")
                .temperature(0.0)
                .maxTokens(15536)
                .build();

        return chatClient.prompt()
                .system("你是一个ai助手")
                .user(query)
                .options(chatOptions)
                .call().content();
    }

    @GetMapping("/book")
    public Book book() {
        ZhiPuAiChatOptions chatOptions = ZhiPuAiChatOptions.builder()
                .model("glm-4.6")
                .temperature(0.0)
                .maxTokens(15536)
                .build();

        return chatClient.prompt()
                .user("给我随机生成一本书，要求书名和作者都是中文")
                .options(chatOptions)
                .call()
                .entity(Book.class);
    }

    @GetMapping("/stream")
    public Flux<String> stream(@RequestParam(name = "query") String query) {
        ZhiPuAiChatOptions chatOptions = ZhiPuAiChatOptions.builder()
                .model("glm-4.6")
                .temperature(0.0)
                .maxTokens(15536)
                .build();

        return chatClient.prompt()
                .system("你是一个ai助手")
                .user(query)
                .options(chatOptions)
                .stream().content();
    }

    @GetMapping("/testAdvisor")
    public Book testAdvisor() {
        ZhiPuAiChatOptions chatOptions = ZhiPuAiChatOptions.builder()
                .model("glm-4.6")
                .temperature(0.0)
                .maxTokens(15536)
                .build();

        return chatClient.prompt()
                .user("给我随机生成一本书，要求书名和作者都是中文")
                .options(chatOptions)
                .advisors(new SGAdvisor2(), new SGAdvisor1())
                .call()
                .entity(Book.class);
    }

    /**
     * 测试 simpleMessageChatMemoryAdvisor
     *
     * @return
     */
    @GetMapping("/simpleAdvisor")
    public String simpleAdvisor(@RequestParam(name = "query") String query, @RequestParam(required = false) String conversationId) {
        ZhiPuAiChatOptions chatOptions = ZhiPuAiChatOptions.builder()
                .model("glm-4.6")
                .temperature(0.0)
                .maxTokens(15536)
                .build();

        String actualConversationId =
                StringUtils.hasText(conversationId)
                        ? conversationId
                        : UUID.randomUUID().toString();

        return chatClient.prompt()
                .user(query)
                .options(chatOptions)
                //设置会话id
                .advisors(advisorSpec -> {
                    advisorSpec.param("conversationId", actualConversationId);
                })
                .advisors(new SimpleMessageChatMemoryAdvisor())
                .call().content();
    }

}
