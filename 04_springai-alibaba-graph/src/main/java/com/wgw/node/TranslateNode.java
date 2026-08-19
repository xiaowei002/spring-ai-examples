package com.wgw.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 翻译node
 */
public class TranslateNode implements NodeAction {

    private final ChatClient chatClient;

    public TranslateNode(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        //获取翻译好的句子
        String sentence = state.value("sentence", "");
        PromptTemplate promptTemplate = new PromptTemplate("你是一个翻译专家，能有基于给定的句子进行翻译"
                + "要求只返回翻译后的句子，不要返回其他信息。要翻译的句子： {sentence}");
        promptTemplate.add("sentence", sentence);
        String prompt = promptTemplate.render();
        //调用大模型
        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        return Map.of("translate", content);
    }
}
