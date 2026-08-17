package com.wgw.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

/**
 * 自定义advisor 2
 */
@Slf4j
public class SGAdvisor2 implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        log.info("advisor2 开始");
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        log.info("advisor2 结束");
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "SGAdvisor2";
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
