package com.goaltracker.dto.config;

import java.math.BigDecimal;

public record TrackAllocationResponse(
        Long trackId,
        String trackName,
        String dayType,
        BigDecimal allocatedHours
) {
}
