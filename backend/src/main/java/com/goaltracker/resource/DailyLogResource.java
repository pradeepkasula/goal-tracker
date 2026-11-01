package com.goaltracker.resource;

import com.goaltracker.dto.log.DailyLogPatchRequest;
import com.goaltracker.dto.log.DailyLogResponse;
import com.goaltracker.jaxrs.PATCH;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.DailyLogService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Path("daily-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DailyLogResource {

    private final DailyLogService dailyLogService;

    public DailyLogResource(DailyLogService dailyLogService) {
        this.dailyLogService = dailyLogService;
    }

    @GET
    public List<DailyLogResponse> list(@Context SecurityContext sc,
                                        @QueryParam("date") String date,
                                        @QueryParam("from") String from,
                                        @QueryParam("to") String to,
                                        @QueryParam("trackId") Long trackId) {
        Long userId = CurrentUserResolver.requireUserId(sc);
        if (date != null) {
            return dailyLogService.getOrCreateForDate(userId, LocalDate.parse(date));
        }
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now().minusDays(30);
        LocalDate toDate = to != null ? LocalDate.parse(to) : LocalDate.now();
        return dailyLogService.listRange(userId, trackId, fromDate, toDate);
    }

    @GET
    @Path("{trackId}/{date}")
    public DailyLogResponse get(@Context SecurityContext sc, @PathParam("trackId") Long trackId,
                                 @PathParam("date") String date) {
        return dailyLogService.getOrCreate(CurrentUserResolver.requireUserId(sc), trackId, LocalDate.parse(date));
    }

    @PATCH
    @Path("{trackId}/{date}")
    public DailyLogResponse patch(@Context SecurityContext sc, @PathParam("trackId") Long trackId,
                                   @PathParam("date") String date, DailyLogPatchRequest request) {
        return dailyLogService.patch(CurrentUserResolver.requireUserId(sc), trackId, LocalDate.parse(date), request);
    }
}
