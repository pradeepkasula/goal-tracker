package com.goaltracker.dto.log;

import java.time.LocalDate;

public record TopicLogResponse(
        Long id,
        Long topicId,
        LocalDate date,
        String status,
        Integer minutesSpent,
        String notes
) {
}
