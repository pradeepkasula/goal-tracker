package com.goaltracker.resource;

import com.goaltracker.domain.enums.DayType;
import com.goaltracker.dto.config.AllocationWarning;
import com.goaltracker.dto.config.TrackAllocationRequest;
import com.goaltracker.dto.config.TrackAllocationResponse;
import com.goaltracker.dto.config.TrackAllocationsResponse;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.GoalConfigService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Path("config/track-allocations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrackAllocationResource {

    private final GoalConfigService goalConfigService;

    public TrackAllocationResource(GoalConfigService goalConfigService) {
        this.goalConfigService = goalConfigService;
    }

    @GET
    public TrackAllocationsResponse list(@Context SecurityContext sc, @QueryParam("dayType") String dayType) {
        DayType parsed = dayType != null ? DayType.valueOf(dayType.toUpperCase()) : null;
        return goalConfigService.listAllocations(CurrentUserResolver.requireUserId(sc), parsed);
    }

    @GET
    @Path("validate")
    public List<AllocationWarning> validate(@Context SecurityContext sc) {
        return goalConfigService.validateAllocations(CurrentUserResolver.requireUserId(sc));
    }

    @PUT
    @Path("{trackId}/{dayType}")
    public TrackAllocationResponse upsert(@Context SecurityContext sc, @PathParam("trackId") Long trackId,
                                           @PathParam("dayType") String dayType, @Valid TrackAllocationRequest request) {
        return goalConfigService.upsertAllocation(CurrentUserResolver.requireUserId(sc), trackId,
                DayType.valueOf(dayType.toUpperCase()), request);
    }
}
