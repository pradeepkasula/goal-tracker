package com.goaltracker.repository;

import com.goaltracker.domain.TrackHourAllocation;
import com.goaltracker.domain.enums.DayType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackHourAllocationRepository extends JpaRepository<TrackHourAllocation, Long> {
    List<TrackHourAllocation> findByUserId(Long userId);
    List<TrackHourAllocation> findByUserIdAndDayType(Long userId, DayType dayType);
    Optional<TrackHourAllocation> findByUserIdAndTrackIdAndDayType(Long userId, Long trackId, DayType dayType);
}
