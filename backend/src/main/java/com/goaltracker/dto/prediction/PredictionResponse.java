package com.goaltracker.dto.prediction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PredictionResponse(
        Long trackId,
        String trackName,
        LocalDate predictedCompletionDate,
        BigDecimal paceTopicsPerWeek,
        BigDecimal coveragePct,
        String confidence,
        String aiNarrative,
        Instant computedAt
) {
}
