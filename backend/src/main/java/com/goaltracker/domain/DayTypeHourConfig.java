package com.goaltracker.domain;

import com.goaltracker.domain.enums.DayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "day_type_hour_config")
@Getter
@Setter
@NoArgsConstructor
public class DayTypeHourConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", nullable = false, length = 16)
    private DayType dayType;

    @Column(name = "total_hours", nullable = false, precision = 4, scale = 2)
    private BigDecimal totalHours;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
