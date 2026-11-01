package com.goaltracker.resource;

import com.goaltracker.dto.log.TopicLogRequest;
import com.goaltracker.dto.log.TopicLogResponse;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.ProgressService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/** Owns /topic-logs - per-topic progress itself lives on TopicResource (/topics/{id}/progress). */
@Component
@Path("topic-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopicLogResource {

    private final ProgressService progressService;

    public TopicLogResource(ProgressService progressService) {
        this.progressService = progressService;
    }

    @POST
    public TopicLogResponse record(@Context SecurityContext sc, @Valid TopicLogRequest request) {
        return progressService.recordTopicLog(CurrentUserResolver.requireUserId(sc), request);
    }

    @GET
    public List<TopicLogResponse> list(@Context SecurityContext sc,
                                        @QueryParam("topicId") Long topicId,
                                        @QueryParam("from") String from,
                                        @QueryParam("to") String to) {
        LocalDate fromDate = from != null ? LocalDate.parse(from) : null;
        LocalDate toDate = to != null ? LocalDate.parse(to) : null;
        return progressService.listLogs(CurrentUserResolver.requireUserId(sc), topicId, fromDate, toDate);
    }
}
