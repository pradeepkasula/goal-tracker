package com.goaltracker.service;

import com.goaltracker.ai.PredictionPromptBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Turns a deterministic {@link StatsSnapshot} into a short narrative via Spring AI. Never throws:
 * any failure to reach the configured model (Ollama down, timeout, model not pulled, etc.) is
 * logged and swallowed, so callers always get a usable (possibly narrative-less) result.
 */
@Service
public class AiInsightService {

    private static final Logger log = LoggerFactory.getLogger(AiInsightService.class);

    private final ChatClient chatClient;
    private final PredictionPromptBuilder promptBuilder;
    private final boolean aiEnabled;

    public AiInsightService(ChatClient chatClient, PredictionPromptBuilder promptBuilder,
                             @Value("${app.ai.enabled}") boolean aiEnabled) {
        this.chatClient = chatClient;
        this.promptBuilder = promptBuilder;
        this.aiEnabled = aiEnabled;
    }

    public Optional<String> generateNarrative(StatsSnapshot stats) {
        if (!aiEnabled) {
            return Optional.empty();
        }
        try {
            String prompt = promptBuilder.build(stats);
            String content = chatClient.prompt().user(prompt).call().content();
            return Optional.ofNullable(content).filter(s -> !s.isBlank());
        } catch (Exception e) {
            log.warn("AI narrative unavailable for track '{}' (falling back to stats-only): {}",
                    stats.trackName(), e.getMessage());
            return Optional.empty();
        }
    }
}
