package com.wgw.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 造句节点
 */
public class GenerateJokeNode implements NodeAction {

    private final ChatClient chatClient;

    public GenerateJokeNode(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        //从state 中获取输入
        String topic = state.value("topic", "");
        //使用提示词模板构建提示词
        PromptTemplate promptTemplate = new PromptTemplate("你需要写一个关于指定主题的短笑话。要求返回的结果中只能包含笑话的内容" +
                "主题： {topic}");
        promptTemplate.add("topic", topic);
        String prompt = promptTemplate.render();
        //调用模型生成笑话
        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        //放入state
        return Map.of("joke", content);
    }
}
