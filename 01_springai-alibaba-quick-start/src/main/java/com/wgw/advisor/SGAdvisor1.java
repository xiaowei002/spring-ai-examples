package com.wgw.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

/**
 * 自定义advisor 1
 *
 */
@Slf4j
public class SGAdvisor1 implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        log.info("advisor1 开始");
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        log.info("advisor1 结束");
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "SGAdvisor1";
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
