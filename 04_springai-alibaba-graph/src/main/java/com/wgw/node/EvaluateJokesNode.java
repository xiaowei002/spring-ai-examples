package com.wgw.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 造句节点
 */
public class EvaluateJokesNode implements NodeAction {

    private final ChatClient chatClient;

    public EvaluateJokesNode(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        //从state 中获取输入
        String joke = state.value("joke", "");
        //使用提示词模板构建提示词
        PromptTemplate promptTemplate = new PromptTemplate("你是一个笑话评分专家，能够对笑话进行频分，基于笑话的搞笑程度给出0到10分的打分" +
                "然后基于评分进行评价。如果大于等于3分评价：优秀。否则评价：不够优秀"
                + "要求结果只返回最后的评价，不要其他内容。"
                + "要评分的笑话： {joke}");
        promptTemplate.add("joke", joke);
        String prompt = promptTemplate.render();
        //调用模型生成笑话
        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        //放入state
        return Map.of("result", content.trim());
    }
}
