package com.wgw.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 造句节点
 */
@Slf4j
public class LoopEvaluateJokesNode implements NodeAction {

    private final ChatClient chatClient;
    private final Integer targetScore;
    private final Integer maxLoopCount;

    public LoopEvaluateJokesNode(ChatClient.Builder builder, Integer targetScore, Integer maxLoopCount) {
        this.chatClient = builder.build();
        this.targetScore = targetScore;
        this.maxLoopCount = maxLoopCount;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        //从state 中获取输入
        String joke = state.value("joke", "");
        Integer loopCount = state.value("loopCount", 1);
        //使用提示词模板构建提示词
        PromptTemplate promptTemplate = new PromptTemplate("你是一个笑话专家，能够对笑话进行评分，基于效果的搞笑程度给出0到10分的打分" +
                "要求打分只能是整数，要求结果值返回最后的打分，不要其他内容。" +
                "要评分的笑话：{joke}");
        promptTemplate.add("joke", joke);
        String prompt = promptTemplate.render();
        //调用模型生成笑话
        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        //根据分数判断继续循环还是结束
        Integer i = Integer.valueOf(content.trim());
        String result = "loop";
        if (i >= targetScore || loopCount >=  maxLoopCount) {
            result = "break";
        }
        log.info("joke {}, score {}, loopCount {}", joke, i, loopCount);
        loopCount++;
        //放入state
        return Map.of("result", result, "loopCount", loopCount);
    }
}
