package com.goaltracker.security;

import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.util.Set;

/**
 * Enforces JWT auth for every JAX-RS request except the whitelisted public paths.
 * Runs inside Jersey's own filter chain (not the Spring Security servlet chain), since Jersey
 * dispatches resource methods directly.
 */
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
@Component
public class JwtAuthFilter implements ContainerRequestFilter {

    private static final Set<String> PUBLIC_PATHS = Set.of("auth/login", "version");

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (isPublic(path)) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortUnauthorized(requestContext, "Missing bearer token");
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            var claims = jwtTokenProvider.parseClaims(token);
            Long userId = Long.valueOf(claims.getSubject());
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);
            AuthenticatedUser principal = new AuthenticatedUser(userId, username, role);
            requestContext.setSecurityContext(new JwtSecurityContext(principal));
        } catch (JwtException | IllegalArgumentException e) {
            abortUnauthorized(requestContext, "Invalid or expired token");
        }
    }

    private boolean isPublic(String path) {
        String normalized = path.startsWith("/") ? path.substring(1) : path;
        return PUBLIC_PATHS.contains(normalized);
    }

    private void abortUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorBody(message))
                        .build());
    }

    private record ErrorBody(String message) {
    }

    private record JwtSecurityContext(AuthenticatedUser principal) implements SecurityContext {
        @Override
        public Principal getUserPrincipal() {
            return principal;
        }

        @Override
        public boolean isUserInRole(String role) {
            return principal.role() != null && principal.role().equalsIgnoreCase(role);
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }
}
