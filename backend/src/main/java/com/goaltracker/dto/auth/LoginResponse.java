package com.goaltracker.dto.auth;

public record LoginResponse(
        String token,
        long expiresInMinutes,
        Long userId,
        String username,
        String displayName,
        String role
) {
}
