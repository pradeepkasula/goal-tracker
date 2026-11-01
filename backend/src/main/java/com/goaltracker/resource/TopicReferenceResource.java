package com.goaltracker.resource;

import com.goaltracker.dto.topic.TopicReferenceRequest;
import com.goaltracker.dto.topic.TopicReferenceResponse;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.TopicService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

/** Owns /references/{id} - listing/adding references for a topic lives on TopicResource instead. */
@Component
@Path("references")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopicReferenceResource {

    private final TopicService topicService;

    public TopicReferenceResource(TopicService topicService) {
        this.topicService = topicService;
    }

    @PUT
    @Path("{referenceId}")
    public TopicReferenceResponse update(@Context SecurityContext sc, @PathParam("referenceId") Long referenceId,
                                          @Valid TopicReferenceRequest request) {
        return topicService.updateReference(CurrentUserResolver.requireUserId(sc), referenceId, request);
    }

    @DELETE
    @Path("{referenceId}")
    public void delete(@Context SecurityContext sc, @PathParam("referenceId") Long referenceId) {
        topicService.deleteReference(CurrentUserResolver.requireUserId(sc), referenceId);
    }
}
