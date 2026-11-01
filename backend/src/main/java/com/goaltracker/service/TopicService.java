package com.goaltracker.service;

import com.goaltracker.domain.Topic;
import com.goaltracker.domain.TopicReference;
import com.goaltracker.domain.enums.ReferenceType;
import com.goaltracker.dto.topic.*;
import com.goaltracker.exception.ResourceNotFoundException;
import com.goaltracker.exception.ValidationException;
import com.goaltracker.repository.TopicReferenceRepository;
import com.goaltracker.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicReferenceRepository referenceRepository;
    private final TrackService trackService;
    private final ProgressService progressService;

    public TopicService(TopicRepository topicRepository, TopicReferenceRepository referenceRepository,
                         TrackService trackService, ProgressService progressService) {
        this.topicRepository = topicRepository;
        this.referenceRepository = referenceRepository;
        this.trackService = trackService;
        this.progressService = progressService;
    }

    public List<TopicResponse> listTopics(Long userId, Long trackId) {
        trackService.findOwned(userId, trackId);
        List<Topic> topics = topicRepository.findByTrackIdOrderBySortOrderAsc(trackId);
        return enrich(userId, topics);
    }

    public TopicResponse getTopic(Long userId, Long topicId) {
        Topic topic = findOwned(userId, topicId);
        return enrich(userId, List.of(topic)).get(0);
    }

    @Transactional
    public TopicResponse createTopic(Long userId, Long trackId, TopicRequest request) {
        trackService.findOwned(userId, trackId);
        if (topicRepository.existsByTrackIdAndTitleIgnoreCase(trackId, request.title())) {
            throw new ValidationException("A topic titled '" + request.title() + "' already exists in this track");
        }
        Topic topic = new Topic();
        topic.setUserId(userId);
        topic.setTrackId(trackId);
        apply(topic, request);
        Topic saved = topicRepository.save(topic);
        return enrich(userId, List.of(saved)).get(0);
    }

    @Transactional
    public TopicResponse updateTopic(Long userId, Long topicId, TopicRequest request) {
        Topic topic = findOwned(userId, topicId);
        apply(topic, request);
        Topic saved = topicRepository.save(topic);
        return enrich(userId, List.of(saved)).get(0);
    }

    @Transactional
    public void deleteTopic(Long userId, Long topicId) {
        Topic topic = findOwned(userId, topicId);
        topicRepository.delete(topic);
    }

    @Transactional
    public void reorder(Long userId, Long trackId, ReorderRequest request) {
        trackService.findOwned(userId, trackId);
        List<Topic> topics = topicRepository.findByTrackIdOrderBySortOrderAsc(trackId);
        Map<Long, Topic> byId = topics.stream().collect(Collectors.toMap(Topic::getId, t -> t));

        int order = 0;
        for (Long topicId : request.orderedTopicIds()) {
            Topic topic = byId.get(topicId);
            if (topic == null) {
                throw new ValidationException("Topic " + topicId + " does not belong to track " + trackId);
            }
            topic.setSortOrder(order++);
        }
        topicRepository.saveAll(byId.values());
    }

    @Transactional
    public TopicReferenceResponse addReference(Long userId, Long topicId, TopicReferenceRequest request) {
        findOwned(userId, topicId);
        TopicReference reference = new TopicReference();
        reference.setUserId(userId);
        reference.setTopicId(topicId);
        applyReference(reference, request);
        return toReferenceResponse(referenceRepository.save(reference));
    }

    @Transactional
    public TopicReferenceResponse updateReference(Long userId, Long referenceId, TopicReferenceRequest request) {
        TopicReference reference = referenceRepository.findByIdAndUserId(referenceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reference not found"));
        applyReference(reference, request);
        return toReferenceResponse(referenceRepository.save(reference));
    }

    @Transactional
    public void deleteReference(Long userId, Long referenceId) {
        TopicReference reference = referenceRepository.findByIdAndUserId(referenceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reference not found"));
        referenceRepository.delete(reference);
    }

    public List<TopicReferenceResponse> listReferences(Long userId, Long topicId) {
        findOwned(userId, topicId);
        return referenceRepository.findByTopicIdOrderBySortOrderAsc(topicId).stream()
                .map(this::toReferenceResponse)
                .toList();
    }

    public Topic findOwned(Long userId, Long topicId) {
        return topicRepository.findByIdAndUserId(topicId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
    }

    private List<TopicResponse> enrich(Long userId, List<Topic> topics) {
        return topics.stream().map(topic -> {
            List<TopicReferenceResponse> refs = referenceRepository.findByTopicIdOrderBySortOrderAsc(topic.getId())
                    .stream().map(this::toReferenceResponse).toList();
            var progress = progressService.getProgress(userId, topic.getId());
            return new TopicResponse(topic.getId(), topic.getTrackId(), topic.getTitle(), topic.getDescription(),
                    topic.getSortOrder(), topic.getEstimatedMinutes(), progress, refs);
        }).toList();
    }

    private void apply(Topic topic, TopicRequest request) {
        topic.setTitle(request.title());
        topic.setDescription(request.description());
        topic.setEstimatedMinutes(request.estimatedMinutes());
        if (request.sortOrder() != null) {
            topic.setSortOrder(request.sortOrder());
        }
    }

    private void applyReference(TopicReference reference, TopicReferenceRequest request) {
        reference.setReferenceType(ReferenceType.valueOf(request.referenceType().toUpperCase()));
        reference.setTitle(request.title());
        reference.setUrl(request.url());
        if (request.sortOrder() != null) {
            reference.setSortOrder(request.sortOrder());
        }
    }

    private TopicReferenceResponse toReferenceResponse(TopicReference reference) {
        return new TopicReferenceResponse(reference.getId(), reference.getReferenceType().name(),
                reference.getTitle(), reference.getUrl(), reference.getSortOrder());
    }
}
