package com.goaltracker.service;

import com.goaltracker.domain.DailyTrackLog;
import com.goaltracker.domain.Topic;
import com.goaltracker.domain.Track;
import com.goaltracker.domain.enums.DailyLogStatus;
import com.goaltracker.domain.enums.TopicProgressStatus;
import com.goaltracker.dto.report.AdherenceReportResponse;
import com.goaltracker.dto.report.TrackCoveragePoint;
import com.goaltracker.dto.report.TrackHoursPoint;
import com.goaltracker.repository.DailyTrackLogRepository;
import com.goaltracker.repository.TopicProgressRepository;
import com.goaltracker.repository.TopicRepository;
import com.goaltracker.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Set<DailyLogStatus> ADHERENT = Set.of(DailyLogStatus.MET, DailyLogStatus.EXCEEDED);

    private final DailyTrackLogRepository dailyTrackLogRepository;
    private final TrackRepository trackRepository;
    private final TopicRepository topicRepository;
    private final TopicProgressRepository topicProgressRepository;

    public ReportService(DailyTrackLogRepository dailyTrackLogRepository, TrackRepository trackRepository,
                          TopicRepository topicRepository, TopicProgressRepository topicProgressRepository) {
        this.dailyTrackLogRepository = dailyTrackLogRepository;
        this.trackRepository = trackRepository;
        this.topicRepository = topicRepository;
        this.topicProgressRepository = topicProgressRepository;
    }

    public AdherenceReportResponse getAdherence(Long userId, LocalDate from, LocalDate to, Long trackId) {
        List<DailyTrackLog> logs = trackId != null
                ? dailyTrackLogRepository.findByUserIdAndTrackIdAndLogDateBetweenOrderByLogDateAsc(userId, trackId, from, to)
                : dailyTrackLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to);

        // group by date; a date is adherent only if every log recorded for it that day is MET/EXCEEDED
        Map<LocalDate, List<DailyTrackLog>> byDate = logs.stream()
                .collect(Collectors.groupingBy(DailyTrackLog::getLogDate, TreeMap::new, Collectors.toList()));

        List<LocalDate> dates = new ArrayList<>(byDate.keySet());
        int adherentDays = 0;
        int currentStreak = 0;
        int longestStreak = 0;
        int runningStreak = 0;
        LocalDate previousDate = null;

        for (LocalDate date : dates) {
            boolean adherent = byDate.get(date).stream().allMatch(l -> ADHERENT.contains(l.getStatus()));
            if (adherent) {
                adherentDays++;
            }
            boolean continuous = previousDate != null && previousDate.plusDays(1).equals(date);
            if (adherent && continuous) {
                runningStreak++;
            } else if (adherent) {
                runningStreak = 1;
            } else {
                runningStreak = 0;
            }
            longestStreak = Math.max(longestStreak, runningStreak);
            previousDate = date;
        }
        currentStreak = runningStreak;

        int totalLoggedDays = dates.size();
        BigDecimal adherencePct = totalLoggedDays == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(adherentDays).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalLoggedDays), 2, RoundingMode.HALF_UP);

        return new AdherenceReportResponse(adherencePct, currentStreak, longestStreak, totalLoggedDays, adherentDays);
    }

    public List<TrackHoursPoint> getHoursByTrack(Long userId, LocalDate from, LocalDate to) {
        List<Track> tracks = trackRepository.findByUserIdOrderBySortOrderAsc(userId);
        List<DailyTrackLog> logs = dailyTrackLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to);
        Map<Long, List<DailyTrackLog>> byTrack = logs.stream().collect(Collectors.groupingBy(DailyTrackLog::getTrackId));

        return tracks.stream().map(track -> {
            List<DailyTrackLog> trackLogs = byTrack.getOrDefault(track.getId(), List.of());
            BigDecimal planned = trackLogs.stream().map(DailyTrackLog::getPlannedHours).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal actual = trackLogs.stream().map(DailyTrackLog::getActualHours).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new TrackHoursPoint(track.getId(), track.getName(), planned, actual);
        }).toList();
    }

    public List<TrackCoveragePoint> getCoverage(Long userId, Long trackId) {
        List<Track> tracks = trackId != null
                ? trackRepository.findByIdAndUserId(trackId, userId).map(List::of).orElse(List.of())
                : trackRepository.findByUserIdOrderBySortOrderAsc(userId);

        return tracks.stream().map(track -> {
            List<Topic> topics = topicRepository.findByTrackIdOrderBySortOrderAsc(track.getId());
            List<Long> topicIds = topics.stream().map(Topic::getId).toList();
            int total = topicIds.size();
            long completed = topicIds.isEmpty() ? 0
                    : topicProgressRepository.countByUserIdAndTopicIdInAndStatus(userId, topicIds, TopicProgressStatus.COMPLETED);
            BigDecimal coveragePct = total == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(completed).multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
            return new TrackCoveragePoint(track.getId(), track.getName(), total, (int) completed, coveragePct);
        }).toList();
    }
}
