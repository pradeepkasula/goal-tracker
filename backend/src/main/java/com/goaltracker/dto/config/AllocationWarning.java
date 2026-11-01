package com.goaltracker.dto.config;

import java.math.BigDecimal;

public record AllocationWarning(
        String dayType,
        BigDecimal configuredTotal,
        BigDecimal allocatedSum,
        BigDecimal delta
) {
}
