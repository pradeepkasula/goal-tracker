package com.goaltracker.resource;

import com.goaltracker.dto.auth.LoginRequest;
import com.goaltracker.dto.auth.LoginResponse;
import com.goaltracker.security.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

@Component
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthenticationService authenticationService;

    public AuthResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @POST
    @Path("login")
    public LoginResponse login(@Valid LoginRequest request) {
        var result = authenticationService.login(request.username(), request.password());
        return new LoginResponse(result.token(), result.expiresInMinutes(), result.userId(),
                result.username(), result.displayName(), result.role());
    }
}
