package com.wgw.controller;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/zhipu")
public class ZhipuChatController {

    private final ChatModel chatModel;

    public ZhipuChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }


    @GetMapping("/hello")
    public String hello() {
        return "hello, zhipu";
    }

    @GetMapping("/simple")
    public String simple(@RequestParam(name = "query") String query) {
        return chatModel.call(query);
    }

    @GetMapping("/message")
    public String message(@RequestParam(name = "query") String query) {
        SystemMessage systemMessage = new SystemMessage("你是一个ai助手");
        UserMessage userMessage = new UserMessage(query);
        return chatModel.call(systemMessage, userMessage);
    }

    @GetMapping("/prompt")
    public ChatResponse prompt(@RequestParam(name = "query") String query) {
        ZhiPuAiChatOptions chatOptions = new ZhiPuAiChatOptions();
        chatOptions.setModel("glm-4.6"); //设置模型
        SystemMessage systemMessage = new SystemMessage("你是一个ai助手");
        UserMessage userMessage = new UserMessage(query);
        //构建prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), chatOptions);
        return chatModel.call(prompt);
    }

    @GetMapping("/chatResponse")
    public String chatResponse(@RequestParam(name = "query") String query) {
        ZhiPuAiChatOptions chatOptions = new ZhiPuAiChatOptions();
        chatOptions.setModel("glm-4.6"); //设置模型
        SystemMessage systemMessage = new SystemMessage("你是一个ai助手");
        UserMessage userMessage = new UserMessage(query);
        //构建prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), chatOptions);
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    @GetMapping("/stream")
    public Flux<ChatResponse> stream(@RequestParam(name = "query") String query) {
        ZhiPuAiChatOptions chatOptions = new ZhiPuAiChatOptions();
        chatOptions.setModel("glm-4.6"); //设置模型
        SystemMessage systemMessage = new SystemMessage("你是一个ai助手");
        UserMessage userMessage = new UserMessage(query);
        //构建prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), chatOptions);
        return chatModel.stream(prompt);
    }

}
