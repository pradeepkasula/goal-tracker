package com.goaltracker.dto.topic;

import java.util.List;

public record TopicResponse(
        Long id,
        Long trackId,
        String title,
        String description,
        Integer sortOrder,
        Integer estimatedMinutes,
        TopicProgressResponse progress,
        List<TopicReferenceResponse> references
) {
}
