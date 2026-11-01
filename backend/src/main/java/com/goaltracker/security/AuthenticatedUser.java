package com.goaltracker.security;

import java.security.Principal;

/** Minimal authenticated-principal carried on the JAX-RS SecurityContext for the lifetime of a request. */
public record AuthenticatedUser(Long userId, String username, String role) implements Principal {
    @Override
    public String getName() {
        return username;
    }
}
