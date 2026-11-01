package com.goaltracker.dto.track;

public record TrackResponse(
        Long id,
        String name,
        String description,
        String colorHex,
        Integer sortOrder,
        boolean archived
) {
}
