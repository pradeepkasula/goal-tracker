package com.goaltracker.resource;

import com.goaltracker.dto.auth.MeResponse;
import com.goaltracker.exception.ResourceNotFoundException;
import com.goaltracker.repository.UserRepository;
import com.goaltracker.security.CurrentUserResolver;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

@Component
@Path("me")
@Produces(MediaType.APPLICATION_JSON)
public class MeResource {

    private final UserRepository userRepository;

    public MeResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GET
    public MeResponse me(@Context SecurityContext securityContext) {
        Long userId = CurrentUserResolver.requireUserId(securityContext);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new MeResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole().name());
    }
}
