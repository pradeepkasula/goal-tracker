package com.goaltracker.dto.topic;

public record TopicReferenceResponse(
        Long id,
        String referenceType,
        String title,
        String url,
        Integer sortOrder
) {
}
