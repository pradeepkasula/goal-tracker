package com.goaltracker.dto.topic;

import java.time.Instant;

public record TopicProgressResponse(
        String status,
        Integer timesPracticed,
        Integer totalMinutesSpent,
        Instant lastPracticedAt,
        Instant completedAt
) {
    public static TopicProgressResponse notStarted() {
        return new TopicProgressResponse("NOT_STARTED", 0, 0, null, null);
    }
}
