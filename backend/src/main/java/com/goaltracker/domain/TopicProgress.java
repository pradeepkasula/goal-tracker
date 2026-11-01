package com.goaltracker.domain;

import com.goaltracker.domain.enums.TopicProgressStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "topic_progress")
@Getter
@Setter
@NoArgsConstructor
public class TopicProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TopicProgressStatus status = TopicProgressStatus.NOT_STARTED;

    @Column(name = "times_practiced", nullable = false)
    private Integer timesPracticed = 0;

    @Column(name = "total_minutes_spent", nullable = false)
    private Integer totalMinutesSpent = 0;

    @Column(name = "last_practiced_at")
    private Instant lastPracticedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
