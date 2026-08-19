package com.wgw.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/graph")
@Slf4j
public class GraphController {

    private final CompiledGraph graph;
    private final CompiledGraph simpleGraph;
    private final CompiledGraph conditionGraph;
    private final CompiledGraph loopGraph;

    public GraphController(@Qualifier("quickStartGraph") CompiledGraph graph,
                           @Qualifier("simpleGraph") CompiledGraph simpleGraph,
                           @Qualifier("conditionGraph") CompiledGraph conditionGraph,
                           @Qualifier("loopGraph") CompiledGraph loopGraph) {
        this.graph = graph;
        this.simpleGraph = simpleGraph;
        this.conditionGraph = conditionGraph;
        this.loopGraph = loopGraph;
    }

    @GetMapping("/input")
    public String input() {
        Optional<OverAllState> call = graph.call(Map.of());
        log.info("call is {}", call);
        return "ok";
    }

    @GetMapping("simpleGraph")
    public Map<String, Object> simpleGraph(@RequestParam("word") String word) {
        Optional<OverAllState> call = simpleGraph.call(Map.of("word", word));
        return call.map(OverAllState::data).get();
    }

    @GetMapping("conditionGraph")
    public Map<String, Object> conditionGraph(@RequestParam("topic") String topic) {
        Optional<OverAllState> call = conditionGraph.call(Map.of("topic", topic));
        return call.map(OverAllState::data).get();
    }

    @GetMapping("loopGraph")
    public Map<String, Object> loopGraph(@RequestParam("topic") String topic) {
        Optional<OverAllState> call = loopGraph.call(Map.of("topic", topic));
        return call.map(OverAllState::data).get();
    }
}
