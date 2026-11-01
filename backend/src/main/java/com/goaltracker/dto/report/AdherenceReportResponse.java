package com.goaltracker.dto.report;

import java.math.BigDecimal;

public record AdherenceReportResponse(
        BigDecimal adherencePct,
        int currentStreak,
        int longestStreak,
        int totalLoggedDays,
        int adherentDays
) {
}
