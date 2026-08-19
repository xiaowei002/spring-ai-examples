package com.wgw.config;

import com.wgw.tool.TimeTools;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Time;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider getToolCallbackProvider(TimeTools tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
