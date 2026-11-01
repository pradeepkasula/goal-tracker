package com.goaltracker.dto.dashboard;

import com.goaltracker.dto.log.DailyLogResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TodayViewResponse(
        LocalDate date,
        String dayType,
        List<DailyLogResponse> trackLogs,
        BigDecimal totalPlannedHours,
        BigDecimal totalActualHours,
        int topicsCompletedToday,
        int topicsTouchedToday
) {
}
