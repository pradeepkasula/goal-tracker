package com.goaltracker.dto.config;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DayHourConfigRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal totalHours
) {
}
