package com.goaltracker.security;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.SecurityContext;

/** Pulls the authenticated user id/role out of the JAX-RS SecurityContext set by {@link JwtAuthFilter}. */
public final class CurrentUserResolver {

    private CurrentUserResolver() {
    }

    public static Long requireUserId(SecurityContext securityContext) {
        return requireUser(securityContext).userId();
    }

    public static AuthenticatedUser requireUser(SecurityContext securityContext) {
        if (securityContext == null || !(securityContext.getUserPrincipal() instanceof AuthenticatedUser user)) {
            throw new NotAuthorizedException("Bearer");
        }
        return user;
    }
}
