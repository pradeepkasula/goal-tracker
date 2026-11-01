package com.goaltracker.repository;

import com.goaltracker.domain.TopicReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicReferenceRepository extends JpaRepository<TopicReference, Long> {
    List<TopicReference> findByTopicIdOrderBySortOrderAsc(Long topicId);
    Optional<TopicReference> findByIdAndUserId(Long id, Long userId);
}
