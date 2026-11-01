package com.goaltracker.dto.config;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TrackAllocationRequest(
        @NotNull @DecimalMin(value = "0.0") BigDecimal allocatedHours
) {
}
