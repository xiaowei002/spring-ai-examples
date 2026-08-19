package com.wgw.config.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/graph")
@Slf4j
public class GraphController {

    private final CompiledGraph graph;

    public GraphController(CompiledGraph graph) {
        this.graph = graph;
    }

    @GetMapping("/input")
    public String input() {
        Optional<OverAllState> call = graph.call(Map.of());
        log.info("call is {}", call);
        return "ok";
    }
}
