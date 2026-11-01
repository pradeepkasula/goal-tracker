package com.goaltracker.dto.log;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyLogResponse(
        Long trackId,
        String trackName,
        LocalDate date,
        String dayType,
        BigDecimal plannedHours,
        BigDecimal actualHours,
        String status,
        String notes
) {
}
