package com.goaltracker.resource;

import com.goaltracker.dto.topic.TopicReferenceRequest;
import com.goaltracker.dto.topic.TopicReferenceResponse;
import com.goaltracker.dto.topic.TopicRequest;
import com.goaltracker.dto.topic.TopicResponse;
import com.goaltracker.dto.topic.TopicProgressResponse;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.ProgressService;
import com.goaltracker.service.TopicService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Owns every path under /topics - a single topic's CRUD, its reference links, and its progress.
 * Topics nested under a track (/tracks/{trackId}/topics) live on TrackResource instead, since
 * JAX-RS root-resource matching is by class-level @Path and splitting it across two classes with
 * an overlapping "tracks" prefix would create an ambiguous match.
 */
@Component
@Path("topics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopicResource {

    private final TopicService topicService;
    private final ProgressService progressService;

    public TopicResource(TopicService topicService, ProgressService progressService) {
        this.topicService = topicService;
        this.progressService = progressService;
    }

    @GET
    @Path("{topicId}")
    public TopicResponse get(@Context SecurityContext sc, @PathParam("topicId") Long topicId) {
        return topicService.getTopic(CurrentUserResolver.requireUserId(sc), topicId);
    }

    @PUT
    @Path("{topicId}")
    public TopicResponse update(@Context SecurityContext sc, @PathParam("topicId") Long topicId, @Valid TopicRequest request) {
        return topicService.updateTopic(CurrentUserResolver.requireUserId(sc), topicId, request);
    }

    @DELETE
    @Path("{topicId}")
    public void delete(@Context SecurityContext sc, @PathParam("topicId") Long topicId) {
        topicService.deleteTopic(CurrentUserResolver.requireUserId(sc), topicId);
    }

    @GET
    @Path("{topicId}/references")
    public List<TopicReferenceResponse> listReferences(@Context SecurityContext sc, @PathParam("topicId") Long topicId) {
        return topicService.listReferences(CurrentUserResolver.requireUserId(sc), topicId);
    }

    @POST
    @Path("{topicId}/references")
    public TopicReferenceResponse addReference(@Context SecurityContext sc, @PathParam("topicId") Long topicId,
                                                @Valid TopicReferenceRequest request) {
        return topicService.addReference(CurrentUserResolver.requireUserId(sc), topicId, request);
    }

    @GET
    @Path("{topicId}/progress")
    public TopicProgressResponse progress(@Context SecurityContext sc, @PathParam("topicId") Long topicId) {
        return progressService.getProgress(CurrentUserResolver.requireUserId(sc), topicId);
    }
}
