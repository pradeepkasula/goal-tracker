package com.goaltracker.ai;

import com.goaltracker.service.StatsSnapshot;
import org.springframework.stereotype.Component;

@Component
public class PredictionPromptBuilder {

    public String build(StatsSnapshot stats) {
        String predictedDate = stats.predictedCompletionDate() != null
                ? stats.predictedCompletionDate().toString()
                : "insufficient data to estimate yet";

        return """
                You are a study coach. Given this learner's progress on "%s":
                - Topics completed: %d/%d (%s%%)
                - Current pace: %s topics/week
                - Predicted completion: %s
                - Recent adherence: %s%% of goal days met, current streak %d days
                - Confidence in this estimate: %s

                Write a 2-3 sentence encouraging but honest summary and one concrete suggestion to \
                improve pace, if needed. Do not invent numbers that are not given above.
                """.formatted(
                stats.trackName(), stats.completedTopics(), stats.totalTopics(), stats.coveragePct(),
                stats.paceTopicsPerWeek(), predictedDate, stats.adherencePct(), stats.currentStreak(),
                stats.confidence());
    }
}
