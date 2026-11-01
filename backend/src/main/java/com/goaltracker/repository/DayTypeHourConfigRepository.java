package com.goaltracker.repository;

import com.goaltracker.domain.DayTypeHourConfig;
import com.goaltracker.domain.enums.DayType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DayTypeHourConfigRepository extends JpaRepository<DayTypeHourConfig, Long> {
    List<DayTypeHourConfig> findByUserId(Long userId);
    Optional<DayTypeHourConfig> findByUserIdAndDayType(Long userId, DayType dayType);
}
