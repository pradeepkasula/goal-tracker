package com.goaltracker.dto.topic;

import jakarta.validation.constraints.NotBlank;

public record TopicRequest(
        @NotBlank String title,
        String description,
        Integer sortOrder,
        Integer estimatedMinutes
) {
}
