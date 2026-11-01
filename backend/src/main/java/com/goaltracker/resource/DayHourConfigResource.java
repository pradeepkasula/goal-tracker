package com.goaltracker.resource;

import com.goaltracker.domain.enums.DayType;
import com.goaltracker.dto.config.DayHourConfigRequest;
import com.goaltracker.dto.config.DayHourConfigResponse;
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
@Path("config/day-hours")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DayHourConfigResource {

    private final GoalConfigService goalConfigService;

    public DayHourConfigResource(GoalConfigService goalConfigService) {
        this.goalConfigService = goalConfigService;
    }

    @GET
    public List<DayHourConfigResponse> list(@Context SecurityContext sc) {
        return goalConfigService.listDayHourConfig(CurrentUserResolver.requireUserId(sc));
    }

    @PUT
    @Path("{dayType}")
    public DayHourConfigResponse update(@Context SecurityContext sc, @PathParam("dayType") String dayType,
                                         @Valid DayHourConfigRequest request) {
        return goalConfigService.updateDayHourConfig(CurrentUserResolver.requireUserId(sc),
                DayType.valueOf(dayType.toUpperCase()), request);
    }
}
