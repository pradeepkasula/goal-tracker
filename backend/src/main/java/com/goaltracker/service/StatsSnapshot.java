package com.goaltracker.service;

import com.goaltracker.domain.enums.Confidence;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Internal result of the deterministic prediction math, before any AI narrative is layered on. */
public record StatsSnapshot(
        Long trackId,
        String trackName,
        int totalTopics,
        int completedTopics,
        BigDecimal coveragePct,
        BigDecimal paceTopicsPerWeek,
        LocalDate predictedCompletionDate,
        Confidence confidence,
        BigDecimal adherencePct,
        int currentStreak
) {
}
