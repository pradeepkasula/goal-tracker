package com.goaltracker.dto.log;

import java.math.BigDecimal;

/** All fields optional - only provided fields are updated. */
public record DailyLogPatchRequest(
        BigDecimal actualHours,
        String status,
        String notes
) {
}
