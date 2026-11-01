package com.goaltracker.resource;

import com.goaltracker.dto.dashboard.TodayViewResponse;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.DashboardService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

@Component
@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    private final DashboardService dashboardService;

    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GET
    @Path("today")
    public TodayViewResponse today(@Context SecurityContext sc) {
        return dashboardService.getToday(CurrentUserResolver.requireUserId(sc));
    }
}
