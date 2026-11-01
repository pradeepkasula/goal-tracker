package com.goaltracker.dto.track;

import jakarta.validation.constraints.NotBlank;

public record TrackRequest(
        @NotBlank String name,
        String description,
        String colorHex,
        Integer sortOrder
) {
}
