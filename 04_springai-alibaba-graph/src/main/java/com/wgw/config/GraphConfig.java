package com.wgw.config;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.*;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.wgw.node.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 状态图配置类
 */
@Configuration
@Slf4j
public class GraphConfig {

    /**
     * 状态图配置
     *
     * @return 编译后的状态图
     * @throws GraphStateException
     */
    @Bean("quickStartGraph")
    public CompiledGraph quickStartGraph() throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("input1", KeyStrategy.REPLACE,
                "input2", KeyStrategy.REPLACE);
        //定义状态图
        StateGraph stateGraph = new StateGraph("quickStartGraph", keyStrategyFactory);
        //定义节点
        stateGraph.addNode("node1", AsyncNodeAction.node_async(new NodeAction() {
            @Override
            public Map<String, Object> apply(OverAllState state) throws Exception {
                log.info("node1: {}", state);
                return Map.of("input1", 1, "input2", 1);
            }
        }));
        stateGraph.addNode("node2", AsyncNodeAction.node_async(new NodeAction() {
            @Override
            public Map<String, Object> apply(OverAllState state) throws Exception {
                log.info("node2: {}", state);
                return Map.of("input1", 2, "input2", 2);
            }
        }));
        //定义边
        stateGraph.addEdge(StateGraph.START, "node1");
        stateGraph.addEdge("node1", "node2");
        stateGraph.addEdge("node2", StateGraph.END);
        //编译节点
        return stateGraph.compile();
    }


    @Bean("simpleGraph")
    public CompiledGraph simpleGraph(ChatClient.Builder builder) throws GraphStateException {
        //定义状态图
        StateGraph stateGraph = new StateGraph("simpleGraph", () -> Map.of(
                "word", KeyStrategy.REPLACE,
                "sentence", KeyStrategy.REPLACE,
                "translate", KeyStrategy.REPLACE));
        //定义节点
        stateGraph.addNode("SentenceConstructionNode", AsyncNodeAction.node_async(new SentenceConstructionNode(builder)));
        stateGraph.addNode("TranslateNode", AsyncNodeAction.node_async(new TranslateNode(builder)));
        //定义边
        stateGraph.addEdge(StateGraph.START, "SentenceConstructionNode");
        stateGraph.addEdge("SentenceConstructionNode", "TranslateNode");
        stateGraph.addEdge("TranslateNode", StateGraph.END);
        //返回编译
        return stateGraph.compile();
    }


    @Bean("conditionGraph")
    public CompiledGraph conditionGraph(ChatClient.Builder builder) throws GraphStateException {
        //定义状态图
        StateGraph stateGraph = new StateGraph("conditionGraph", () -> Map.of(
                "topic", KeyStrategy.REPLACE));
        //定义节点
        stateGraph.addNode("生成笑话", AsyncNodeAction.node_async(new GenerateJokeNode(builder)));
        stateGraph.addNode("评价笑话", AsyncNodeAction.node_async(new EvaluateJokesNode(builder)));
        stateGraph.addNode("优化笑话", AsyncNodeAction.node_async(new EnhancejokeQualityNode(builder)));
        //定义边
        stateGraph.addEdge(StateGraph.START, "生成笑话");
        stateGraph.addEdge("生成笑话", "评价笑话");
        //条件边
        stateGraph.addConditionalEdges("评价笑话", AsyncEdgeAction.edge_async(new EdgeAction() {
                    @Override
                    public String apply(OverAllState state) throws Exception {
                        return state.value("result", "优秀");
                    }
                })
                , Map.of("优秀", StateGraph.END,
                        "不够优秀", "优化笑话"));
        stateGraph.addEdge("优化笑话", StateGraph.END);
        //返回编译
        return stateGraph.compile();
    }



    @Bean("loopGraph")
    public CompiledGraph loopGraph(ChatClient.Builder builder) throws GraphStateException {
        //定义状态图
        StateGraph stateGraph = new StateGraph("loopGraph", () -> Map.of(
                "topic", KeyStrategy.REPLACE));
        //定义节点
        stateGraph.addNode("生成笑话", AsyncNodeAction.node_async(new GenerateJokeNode(builder)));
        stateGraph.addNode("评价笑话", AsyncNodeAction.node_async(new LoopEvaluateJokesNode(builder, 7, 3)));
        //定义边
        stateGraph.addEdge(StateGraph.START, "生成笑话");
        stateGraph.addEdge("生成笑话", "评价笑话");
        //条件边
        stateGraph.addConditionalEdges("评价笑话", AsyncEdgeAction.edge_async(state -> state.value("result", "break"))
                , Map.of("break", StateGraph.END,
                        "loop", "生成笑话"));
        //返回编译
        return stateGraph.compile();
    }

}
