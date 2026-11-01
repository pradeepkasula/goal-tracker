package com.goaltracker.dto.config;

import java.math.BigDecimal;

public record DayHourConfigResponse(
        String dayType,
        BigDecimal totalHours
) {
}
