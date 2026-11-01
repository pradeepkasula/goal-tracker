package com.goaltracker.service;

import com.goaltracker.domain.DayTypeHourConfig;
import com.goaltracker.domain.Track;
import com.goaltracker.domain.TrackHourAllocation;
import com.goaltracker.domain.enums.DayType;
import com.goaltracker.dto.config.*;
import com.goaltracker.exception.ResourceNotFoundException;
import com.goaltracker.repository.DayTypeHourConfigRepository;
import com.goaltracker.repository.TrackHourAllocationRepository;
import com.goaltracker.repository.TrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoalConfigService {

    private static final BigDecimal WARNING_EPSILON = new BigDecimal("0.01");

    private final DayTypeHourConfigRepository dayTypeHourConfigRepository;
    private final TrackHourAllocationRepository trackHourAllocationRepository;
    private final TrackRepository trackRepository;

    public GoalConfigService(DayTypeHourConfigRepository dayTypeHourConfigRepository,
                              TrackHourAllocationRepository trackHourAllocationRepository,
                              TrackRepository trackRepository) {
        this.dayTypeHourConfigRepository = dayTypeHourConfigRepository;
        this.trackHourAllocationRepository = trackHourAllocationRepository;
        this.trackRepository = trackRepository;
    }

    public List<DayHourConfigResponse> listDayHourConfig(Long userId) {
        return dayTypeHourConfigRepository.findByUserId(userId).stream()
                .map(c -> new DayHourConfigResponse(c.getDayType().name(), c.getTotalHours()))
                .toList();
    }

    @Transactional
    public DayHourConfigResponse updateDayHourConfig(Long userId, DayType dayType, DayHourConfigRequest request) {
        DayTypeHourConfig config = dayTypeHourConfigRepository.findByUserIdAndDayType(userId, dayType)
                .orElseGet(() -> {
                    DayTypeHourConfig c = new DayTypeHourConfig();
                    c.setUserId(userId);
                    c.setDayType(dayType);
                    return c;
                });
        config.setTotalHours(request.totalHours());
        DayTypeHourConfig saved = dayTypeHourConfigRepository.save(config);
        return new DayHourConfigResponse(saved.getDayType().name(), saved.getTotalHours());
    }

    public TrackAllocationsResponse listAllocations(Long userId, DayType dayType) {
        List<Track> tracks = trackRepository.findByUserIdAndArchivedFalseOrderBySortOrderAsc(userId);
        Map<Long, String> trackNames = tracks.stream().collect(Collectors.toMap(Track::getId, Track::getName));

        List<TrackHourAllocation> allocations = dayType != null
                ? trackHourAllocationRepository.findByUserIdAndDayType(userId, dayType)
                : trackHourAllocationRepository.findByUserId(userId);

        List<TrackAllocationResponse> responses = allocations.stream()
                .filter(a -> trackNames.containsKey(a.getTrackId()))
                .map(a -> new TrackAllocationResponse(a.getTrackId(), trackNames.get(a.getTrackId()),
                        a.getDayType().name(), a.getAllocatedHours()))
                .toList();

        return new TrackAllocationsResponse(responses, validateAllocations(userId));
    }

    @Transactional
    public TrackAllocationResponse upsertAllocation(Long userId, Long trackId, DayType dayType, TrackAllocationRequest request) {
        Track track = trackRepository.findByIdAndUserId(trackId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found"));

        TrackHourAllocation allocation = trackHourAllocationRepository
                .findByUserIdAndTrackIdAndDayType(userId, trackId, dayType)
                .orElseGet(() -> {
                    TrackHourAllocation a = new TrackHourAllocation();
                    a.setUserId(userId);
                    a.setTrackId(trackId);
                    a.setDayType(dayType);
                    return a;
                });
        allocation.setAllocatedHours(request.allocatedHours());
        TrackHourAllocation saved = trackHourAllocationRepository.save(allocation);
        return new TrackAllocationResponse(saved.getTrackId(), track.getName(), saved.getDayType().name(), saved.getAllocatedHours());
    }

    /** Non-fatal check: sums allocations per day type against the configured budget. Never throws. */
    public List<AllocationWarning> validateAllocations(Long userId) {
        Map<DayType, BigDecimal> budgets = dayTypeHourConfigRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(DayTypeHourConfig::getDayType, DayTypeHourConfig::getTotalHours));

        Map<DayType, BigDecimal> allocatedSums = trackHourAllocationRepository.findByUserId(userId).stream()
                .collect(Collectors.groupingBy(TrackHourAllocation::getDayType,
                        Collectors.reducing(BigDecimal.ZERO, TrackHourAllocation::getAllocatedHours, BigDecimal::add)));

        return budgets.entrySet().stream()
                .map(entry -> {
                    DayType dayType = entry.getKey();
                    BigDecimal total = entry.getValue();
                    BigDecimal allocated = allocatedSums.getOrDefault(dayType, BigDecimal.ZERO);
                    BigDecimal delta = total.subtract(allocated);
                    if (delta.abs().compareTo(WARNING_EPSILON) > 0) {
                        return new AllocationWarning(dayType.name(), total, allocated, delta);
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
