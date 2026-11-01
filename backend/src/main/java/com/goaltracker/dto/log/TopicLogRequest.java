package com.goaltracker.dto.log;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TopicLogRequest(
        @NotNull Long topicId,
        @NotNull LocalDate date,
        @NotNull String status,
        Integer minutesSpent,
        String notes
) {
}
