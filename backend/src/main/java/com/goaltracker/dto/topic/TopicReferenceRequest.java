package com.goaltracker.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TopicReferenceRequest(
        @NotNull String referenceType,
        @NotBlank String title,
        @NotBlank String url,
        Integer sortOrder
) {
}
