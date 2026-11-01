package com.goaltracker.resource;

import com.goaltracker.dto.report.AdherenceReportResponse;
import com.goaltracker.dto.report.TrackCoveragePoint;
import com.goaltracker.dto.report.TrackHoursPoint;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.ReportService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Path("reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

    private final ReportService reportService;

    public ReportResource(ReportService reportService) {
        this.reportService = reportService;
    }

    @GET
    @Path("adherence")
    public AdherenceReportResponse adherence(@Context SecurityContext sc,
                                              @QueryParam("from") String from,
                                              @QueryParam("to") String to,
                                              @QueryParam("trackId") Long trackId) {
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now().minusDays(30);
        LocalDate toDate = to != null ? LocalDate.parse(to) : LocalDate.now();
        return reportService.getAdherence(CurrentUserResolver.requireUserId(sc), fromDate, toDate, trackId);
    }

    @GET
    @Path("hours-by-track")
    public List<TrackHoursPoint> hoursByTrack(@Context SecurityContext sc,
                                               @QueryParam("from") String from,
                                               @QueryParam("to") String to) {
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now().minusDays(30);
        LocalDate toDate = to != null ? LocalDate.parse(to) : LocalDate.now();
        return reportService.getHoursByTrack(CurrentUserResolver.requireUserId(sc), fromDate, toDate);
    }

    @GET
    @Path("coverage")
    public List<TrackCoveragePoint> coverage(@Context SecurityContext sc, @QueryParam("trackId") Long trackId) {
        return reportService.getCoverage(CurrentUserResolver.requireUserId(sc), trackId);
    }
}
