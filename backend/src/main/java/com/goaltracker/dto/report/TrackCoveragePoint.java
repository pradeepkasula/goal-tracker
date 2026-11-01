package com.goaltracker.dto.report;

import java.math.BigDecimal;

public record TrackCoveragePoint(
        Long trackId,
        String trackName,
        int totalTopics,
        int completedTopics,
        BigDecimal coveragePct
) {
}
