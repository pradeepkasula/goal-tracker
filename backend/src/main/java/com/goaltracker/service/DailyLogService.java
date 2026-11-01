package com.goaltracker.service;

import com.goaltracker.domain.DailyTrackLog;
import com.goaltracker.domain.Track;
import com.goaltracker.domain.enums.DailyLogStatus;
import com.goaltracker.domain.enums.DayType;
import com.goaltracker.dto.log.DailyLogPatchRequest;
import com.goaltracker.dto.log.DailyLogResponse;
import com.goaltracker.repository.DailyTrackLogRepository;
import com.goaltracker.repository.TrackHourAllocationRepository;
import com.goaltracker.repository.TrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Owns the planned-hours snapshot semantics: a daily_track_log row's planned_hours is fixed the
 * moment it is first created from the then-current track_hour_allocation, so later config edits
 * never rewrite history.
 */
@Service
public class DailyLogService {

    private final DailyTrackLogRepository dailyTrackLogRepository;
    private final TrackHourAllocationRepository trackHourAllocationRepository;
    private final TrackRepository trackRepository;
    private final DayTypeResolver dayTypeResolver;

    public DailyLogService(DailyTrackLogRepository dailyTrackLogRepository,
                            TrackHourAllocationRepository trackHourAllocationRepository,
                            TrackRepository trackRepository,
                            DayTypeResolver dayTypeResolver) {
        this.dailyTrackLogRepository = dailyTrackLogRepository;
        this.trackHourAllocationRepository = trackHourAllocationRepository;
        this.trackRepository = trackRepository;
        this.dayTypeResolver = dayTypeResolver;
    }

    @Transactional
    public List<DailyLogResponse> getOrCreateForDate(Long userId, LocalDate date) {
        List<Track> tracks = trackRepository.findByUserIdAndArchivedFalseOrderBySortOrderAsc(userId);
        return tracks.stream()
                .map(track -> getOrCreate(userId, track, date))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DailyLogResponse getOrCreate(Long userId, Long trackId, LocalDate date) {
        Track track = trackRepository.findByIdAndUserId(trackId, userId)
                .orElseThrow(() -> new com.goaltracker.exception.ResourceNotFoundException("Track not found"));
        return toResponse(getOrCreate(userId, track, date));
    }

    public List<DailyLogResponse> listRange(Long userId, Long trackId, LocalDate from, LocalDate to) {
        List<DailyTrackLog> logs = trackId != null
                ? dailyTrackLogRepository.findByUserIdAndTrackIdAndLogDateBetweenOrderByLogDateAsc(userId, trackId, from, to)
                : dailyTrackLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to);
        Map<Long, String> trackNames = trackRepository.findByUserIdOrderBySortOrderAsc(userId).stream()
                .collect(Collectors.toMap(Track::getId, Track::getName));
        return logs.stream().map(log -> toResponse(log, trackNames.get(log.getTrackId()))).toList();
    }

    @Transactional
    public DailyLogResponse patch(Long userId, Long trackId, LocalDate date, DailyLogPatchRequest request) {
        Track track = trackRepository.findByIdAndUserId(trackId, userId)
                .orElseThrow(() -> new com.goaltracker.exception.ResourceNotFoundException("Track not found"));
        DailyTrackLog log = getOrCreate(userId, track, date);

        if (request.actualHours() != null) {
            log.setActualHours(request.actualHours());
        }
        if (request.notes() != null) {
            log.setNotes(request.notes());
        }
        if (request.status() != null) {
            log.setStatus(DailyLogStatus.valueOf(request.status().toUpperCase()));
        } else {
            log.setStatus(deriveStatus(log.getActualHours(), log.getPlannedHours()));
        }

        return toResponse(dailyTrackLogRepository.save(log), track.getName());
    }

    private DailyTrackLog getOrCreate(Long userId, Track track, LocalDate date) {
        return dailyTrackLogRepository.findByUserIdAndTrackIdAndLogDate(userId, track.getId(), date)
                .orElseGet(() -> {
                    DayType dayType = dayTypeResolver.resolve(userId, date);
                    BigDecimal plannedHours = trackHourAllocationRepository
                            .findByUserIdAndTrackIdAndDayType(userId, track.getId(), dayType)
                            .map(a -> a.getAllocatedHours())
                            .orElse(BigDecimal.ZERO);

                    DailyTrackLog log = new DailyTrackLog();
                    log.setUserId(userId);
                    log.setTrackId(track.getId());
                    log.setLogDate(date);
                    log.setDayType(dayType);
                    log.setPlannedHours(plannedHours);
                    log.setActualHours(BigDecimal.ZERO);
                    log.setStatus(DailyLogStatus.PENDING);
                    return dailyTrackLogRepository.save(log);
                });
    }

    private DailyLogStatus deriveStatus(BigDecimal actual, BigDecimal planned) {
        if (actual.compareTo(BigDecimal.ZERO) == 0) {
            return DailyLogStatus.PENDING;
        }
        int cmp = actual.compareTo(planned);
        if (cmp < 0) {
            return DailyLogStatus.PARTIAL;
        }
        if (cmp == 0) {
            return DailyLogStatus.MET;
        }
        return DailyLogStatus.EXCEEDED;
    }

    private DailyLogResponse toResponse(DailyTrackLog log) {
        String trackName = trackRepository.findById(log.getTrackId()).map(Track::getName).orElse(null);
        return toResponse(log, trackName);
    }

    private DailyLogResponse toResponse(DailyTrackLog log, String trackName) {
        return new DailyLogResponse(log.getTrackId(), trackName, log.getLogDate(), log.getDayType().name(),
                log.getPlannedHours(), log.getActualHours(), log.getStatus().name(), log.getNotes());
    }
}
