package com.goaltracker.resource;

import com.goaltracker.dto.prediction.PredictionResponse;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.PredictionService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

@Component
@Path("predictions")
@Produces(MediaType.APPLICATION_JSON)
public class PredictionResource {

    private final PredictionService predictionService;

    public PredictionResource(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GET
    @Path("tracks/{trackId}")
    public PredictionResponse forTrack(@Context SecurityContext sc, @PathParam("trackId") Long trackId) {
        return predictionService.getForTrack(CurrentUserResolver.requireUserId(sc), trackId, false);
    }

    @GET
    @Path("overall")
    public PredictionResponse overall(@Context SecurityContext sc) {
        return predictionService.getOverall(CurrentUserResolver.requireUserId(sc), false);
    }

    @POST
    @Path("tracks/{trackId}/refresh")
    public PredictionResponse refreshTrack(@Context SecurityContext sc, @PathParam("trackId") Long trackId) {
        return predictionService.getForTrack(CurrentUserResolver.requireUserId(sc), trackId, true);
    }

    @POST
    @Path("overall/refresh")
    public PredictionResponse refreshOverall(@Context SecurityContext sc) {
        return predictionService.getOverall(CurrentUserResolver.requireUserId(sc), true);
    }
}
