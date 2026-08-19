package com.wgw.config;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import lombok.extern.slf4j.Slf4j;
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

}
