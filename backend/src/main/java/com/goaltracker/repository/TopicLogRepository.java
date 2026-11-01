package com.goaltracker.repository;

import com.goaltracker.domain.TopicLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TopicLogRepository extends JpaRepository<TopicLog, Long> {
    List<TopicLog> findByUserIdAndTopicIdOrderByLogDateDesc(Long userId, Long topicId);
    List<TopicLog> findByUserIdAndTopicIdAndLogDateBetweenOrderByLogDateAsc(Long userId, Long topicId, LocalDate from, LocalDate to);
}
