package com.goaltracker.repository;

import com.goaltracker.domain.TopicProgress;
import com.goaltracker.domain.enums.TopicProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TopicProgressRepository extends JpaRepository<TopicProgress, Long> {
    Optional<TopicProgress> findByUserIdAndTopicId(Long userId, Long topicId);
    List<TopicProgress> findByUserIdAndTopicIdIn(Long userId, List<Long> topicIds);
    long countByUserIdAndTopicIdInAndStatus(Long userId, List<Long> topicIds, TopicProgressStatus status);
    long countByUserIdAndTopicIdInAndCompletedAtBetween(Long userId, List<Long> topicIds, Instant from, Instant to);
}
