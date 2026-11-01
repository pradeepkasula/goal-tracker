package com.goaltracker.dto.auth;

public record MeResponse(
        Long id,
        String username,
        String displayName,
        String role
) {
}
