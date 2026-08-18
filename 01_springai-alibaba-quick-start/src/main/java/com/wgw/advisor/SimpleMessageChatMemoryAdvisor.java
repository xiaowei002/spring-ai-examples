package com.wgw.advisor;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;


/**
 * simple memory implement
 * 自定义记忆
 */
public class SimpleMessageChatMemoryAdvisor implements BaseAdvisor {
    private static final Map<String, List<Message>> CACHE = new HashMap<>();


    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        //通过会话id查询会话记录
        String conversationId = chatClientRequest.context().get("conversationId").toString();

        List<Message> messages = CACHE.get(conversationId);
        if (Objects.isNull(messages)) {
            messages = new ArrayList<>();
        }
        //把这次请求的消息添加到对话记录中
        List<Message> instructions = chatClientRequest.prompt().getInstructions();
        messages.addAll(instructions);
        CACHE.put(conversationId, messages);
        //把添加后的记录List<Message> 放入请求
        Prompt oldPrompt = chatClientRequest.prompt();
        Prompt newPrompt = oldPrompt.mutate().messages(messages).build();
        ChatClientRequest request = chatClientRequest.mutate()
                .prompt(newPrompt)
                .build();

        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        //通过会话id获取List<Message>
        String conversationId = chatClientResponse.context().get("conversationId").toString();
        List<Message> messages = CACHE.get(conversationId);
        //将返回的信息添加到message
        ChatResponse chatResponse = chatClientResponse.chatResponse();
        if (Objects.isNull(chatResponse)) {
            throw new RuntimeException("");
        }

        List<AssistantMessage> assistantMessages = chatResponse
                .getResults()
                .stream()
                .map(Generation::getOutput)
                .toList();
        messages.addAll(assistantMessages);
        CACHE.put(conversationId, messages);
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
