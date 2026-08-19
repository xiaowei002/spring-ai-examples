package com.wgw.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 造句节点
 */
public class EnhancejokeQualityNode implements NodeAction {

    private final ChatClient chatClient;

    public EnhancejokeQualityNode(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        //从state 中获取输入
        String joke = state.value("joke", "");
        //使用提示词模板构建提示词
        PromptTemplate promptTemplate = new PromptTemplate("你是一个笑话优化专家，你能够优化笑话，让它更加搞笑" +
                "要求只返回优化的结果不要返回其他信息， 要优化的笑话： {joke}");
        promptTemplate.add("joke", joke);
        String prompt = promptTemplate.render();
        //调用模型生成笑话
        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        //放入state
        return Map.of("newJoke", content);
    }
}
