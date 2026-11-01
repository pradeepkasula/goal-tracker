package com.goaltracker.resource;

import com.goaltracker.dto.topic.ReorderRequest;
import com.goaltracker.dto.topic.TopicRequest;
import com.goaltracker.dto.topic.TopicResponse;
import com.goaltracker.dto.track.TrackRequest;
import com.goaltracker.dto.track.TrackResponse;
import com.goaltracker.jaxrs.PATCH;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.TopicService;
import com.goaltracker.service.TrackService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Owns every path under /tracks, including the topics nested under a track - JAX-RS resolves
 * root resources by class-level @Path, so topic-under-track endpoints must live here rather than
 * on TopicResource (which owns /topics/**) to avoid an ambiguous root-resource match.
 */
@Component
@Path("tracks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrackResource {

    private final TrackService trackService;
    private final TopicService topicService;

    public TrackResource(TrackService trackService, TopicService topicService) {
        this.trackService = trackService;
        this.topicService = topicService;
    }

    @GET
    public List<TrackResponse> list(@Context SecurityContext sc,
                                     @QueryParam("includeArchived") @DefaultValue("false") boolean includeArchived) {
        return trackService.listTracks(CurrentUserResolver.requireUserId(sc), includeArchived);
    }

    @POST
    public TrackResponse create(@Context SecurityContext sc, @Valid TrackRequest request) {
        return trackService.createTrack(CurrentUserResolver.requireUserId(sc), request);
    }

    @GET
    @Path("{trackId}")
    public TrackResponse get(@Context SecurityContext sc, @PathParam("trackId") Long trackId) {
        return trackService.getTrack(CurrentUserResolver.requireUserId(sc), trackId);
    }

    @PUT
    @Path("{trackId}")
    public TrackResponse update(@Context SecurityContext sc, @PathParam("trackId") Long trackId, @Valid TrackRequest request) {
        return trackService.updateTrack(CurrentUserResolver.requireUserId(sc), trackId, request);
    }

    @DELETE
    @Path("{trackId}")
    public void delete(@Context SecurityContext sc, @PathParam("trackId") Long trackId) {
        trackService.deleteTrack(CurrentUserResolver.requireUserId(sc), trackId);
    }

    @PATCH
    @Path("{trackId}/archive")
    public TrackResponse archive(@Context SecurityContext sc, @PathParam("trackId") Long trackId, Map<String, Boolean> body) {
        boolean archived = body.getOrDefault("archived", true);
        return trackService.setArchived(CurrentUserResolver.requireUserId(sc), trackId, archived);
    }

    @GET
    @Path("{trackId}/topics")
    public List<TopicResponse> listTopics(@Context SecurityContext sc, @PathParam("trackId") Long trackId) {
        return topicService.listTopics(CurrentUserResolver.requireUserId(sc), trackId);
    }

    @POST
    @Path("{trackId}/topics")
    public TopicResponse createTopic(@Context SecurityContext sc, @PathParam("trackId") Long trackId, @Valid TopicRequest request) {
        return topicService.createTopic(CurrentUserResolver.requireUserId(sc), trackId, request);
    }

    @PATCH
    @Path("{trackId}/topics/reorder")
    public void reorderTopics(@Context SecurityContext sc, @PathParam("trackId") Long trackId, @Valid ReorderRequest request) {
        topicService.reorder(CurrentUserResolver.requireUserId(sc), trackId, request);
    }
}
