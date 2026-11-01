package com.goaltracker.repository;

import com.goaltracker.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByTrackIdOrderBySortOrderAsc(Long trackId);
    List<Topic> findByUserIdOrderBySortOrderAsc(Long userId);
    Optional<Topic> findByIdAndUserId(Long id, Long userId);
    long countByTrackId(Long trackId);
    boolean existsByTrackIdAndTitleIgnoreCase(Long trackId, String title);
}
