package com.goaltracker.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes a single provider-agnostic {@link ChatClient} bean. Spring AI auto-configures the
 * {@link ChatClient.Builder} from whichever chat-model starter is on the classpath - today that's
 * spring-ai-starter-model-ollama (local, offline-friendly). Swapping to OpenAI/Anthropic later is
 * a matter of adding that starter and its properties; AiInsightService only ever depends on this
 * ChatClient interface, never a provider-specific class.
 */
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
