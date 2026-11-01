package com.goaltracker.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Path("version")
@Produces(MediaType.APPLICATION_JSON)
public class VersionResource {

    private final String version;

    public VersionResource(@Value("${app.version}") String version) {
        this.version = version;
    }

    public record VersionResponse(String version) {
    }

    @GET
    public VersionResponse getVersion() {
        return new VersionResponse(version);
    }
}
