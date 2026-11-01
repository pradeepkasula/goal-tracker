package com.goaltracker.dto.config;

import java.util.List;

public record TrackAllocationsResponse(
        List<TrackAllocationResponse> allocations,
        List<AllocationWarning> warnings
) {
}
