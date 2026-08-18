package com.wgw.controller;

import com.wgw.advisor.SGAdvisor1;
import com.wgw.advisor.SGAdvisor2;
import com.wgw.advisor.SimpleMessageChatMemoryAdvisor;
import com.wgw.model.Book;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * 使用chat memory 来实现记忆
 */
@RestController
@RequestMapping("/chatMemory")
public class ZhipuChatMemoryController {

    private final ChatClient chatClient;

    public ZhipuChatMemoryController(ChatClient.Builder chatClient) {
        MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory
                .builder()
                .build();

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(messageWindowChatMemory)
                .build();

        this.chatClient = chatClient
                .defaultAdvisors(messageChatMemoryAdvisor)
                .build();
    }


    /**
     * 测试 Message Chat Memory Advisor
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
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, actualConversationId);
                })
                .call().content();
    }

}
