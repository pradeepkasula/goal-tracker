package com.goaltracker.dto.report;

import java.math.BigDecimal;

public record TrackHoursPoint(
        Long trackId,
        String trackName,
        BigDecimal plannedHours,
        BigDecimal actualHours
) {
}
