package com.goaltracker.dto.topic;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** Full new ordering of topic ids within a track; position in the list becomes the new sortOrder. */
public record ReorderRequest(
        @NotEmpty List<Long> orderedTopicIds
) {
}
