package com.goaltracker.domain;

import com.goaltracker.domain.enums.Confidence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "prediction_snapshot")
@Getter
@Setter
@NoArgsConstructor
public class PredictionSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** null = overall (cross-track) snapshot */
    @Column(name = "track_id")
    private Long trackId;

    @Column(name = "predicted_completion_date")
    private LocalDate predictedCompletionDate;

    @Column(name = "pace_topics_per_week", precision = 6, scale = 2)
    private BigDecimal paceTopicsPerWeek;

    @Column(name = "coverage_pct", precision = 5, scale = 2)
    private BigDecimal coveragePct;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Confidence confidence;

    @Column(name = "ai_narrative", columnDefinition = "TEXT")
    private String aiNarrative;

    @Column(name = "ai_narrative_generated_at")
    private Instant aiNarrativeGeneratedAt;

    @Column(name = "computed_at", nullable = false)
    private Instant computedAt = Instant.now();
}
