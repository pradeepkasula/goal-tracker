package com.goaltracker.service;

import com.goaltracker.domain.DailyTrackLog;
import com.goaltracker.domain.Topic;
import com.goaltracker.domain.Track;
import com.goaltracker.domain.enums.Confidence;
import com.goaltracker.domain.enums.TopicProgressStatus;
import com.goaltracker.dto.report.AdherenceReportResponse;
import com.goaltracker.repository.DailyTrackLogRepository;
import com.goaltracker.repository.TopicProgressRepository;
import com.goaltracker.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Pure, deterministic pace/ETA/coverage math - no AI, no caching. */
@Service
public class PredictionStatsService {

    private static final int DEFAULT_ESTIMATED_MINUTES = 90;

    private final TopicRepository topicRepository;
    private final TopicProgressRepository topicProgressRepository;
    private final DailyTrackLogRepository dailyTrackLogRepository;
    private final ReportService reportService;
    private final int lookbackDays;

    public PredictionStatsService(TopicRepository topicRepository, TopicProgressRepository topicProgressRepository,
                                   DailyTrackLogRepository dailyTrackLogRepository, ReportService reportService,
                                   @Value("${app.prediction.lookback-days}") int lookbackDays) {
        this.topicRepository = topicRepository;
        this.topicProgressRepository = topicProgressRepository;
        this.dailyTrackLogRepository = dailyTrackLogRepository;
        this.reportService = reportService;
        this.lookbackDays = lookbackDays;
    }

    public StatsSnapshot computeForTrack(Long userId, Track track) {
        LocalDate today = LocalDate.now();
        List<Topic> topics = topicRepository.findByTrackIdOrderBySortOrderAsc(track.getId());
        List<Long> topicIds = topics.stream().map(Topic::getId).toList();
        int totalTopics = topicIds.size();

        long completedTopics = topicIds.isEmpty() ? 0
                : topicProgressRepository.countByUserIdAndTopicIdInAndStatus(userId, topicIds, TopicProgressStatus.COMPLETED);

        int windowDays = windowDays(track.getCreatedAt(), today);
        LocalDate windowStart = today.minusDays(windowDays);

        long completedInWindow = topicIds.isEmpty() ? 0
                : topicProgressRepository.countByUserIdAndTopicIdInAndCompletedAtBetween(
                        userId, topicIds, windowStart.atStartOfDay(java.time.ZoneOffset.UTC).toInstant(), Instant.now());

        BigDecimal paceTopicsPerWeek = computePace(userId, track.getId(), topics, completedInWindow, windowDays, windowStart, today);

        int remainingTopics = Math.max(0, totalTopics - (int) completedTopics);
        LocalDate predictedCompletionDate = predictCompletionDate(today, remainingTopics, paceTopicsPerWeek);

        BigDecimal coveragePct = coveragePct(totalTopics, completedTopics);

        AdherenceReportResponse adherence = reportService.getAdherence(userId, windowStart, today, track.getId());
        Confidence confidence = confidenceFor(windowDays, completedInWindow, adherence.totalLoggedDays());

        return new StatsSnapshot(track.getId(), track.getName(), totalTopics, (int) completedTopics, coveragePct,
                paceTopicsPerWeek, predictedCompletionDate, confidence, adherence.adherencePct(), adherence.currentStreak());
    }

    public StatsSnapshot computeOverall(Long userId, List<Track> tracks) {
        LocalDate today = LocalDate.now();
        int totalTopics = 0;
        long completedTopics = 0;
        long completedInWindow = 0;
        int windowDays = lookbackDays;

        for (Track track : tracks) {
            List<Long> topicIds = topicRepository.findByTrackIdOrderBySortOrderAsc(track.getId())
                    .stream().map(Topic::getId).toList();
            totalTopics += topicIds.size();
            if (topicIds.isEmpty()) {
                continue;
            }
            completedTopics += topicProgressRepository.countByUserIdAndTopicIdInAndStatus(userId, topicIds, TopicProgressStatus.COMPLETED);
            int trackWindow = windowDays(track.getCreatedAt(), today);
            windowDays = Math.min(windowDays, Math.max(trackWindow, 1));
        }

        LocalDate windowStart = today.minusDays(windowDays);
        for (Track track : tracks) {
            List<Long> topicIds = topicRepository.findByTrackIdOrderBySortOrderAsc(track.getId())
                    .stream().map(Topic::getId).toList();
            if (topicIds.isEmpty()) {
                continue;
            }
            completedInWindow += topicProgressRepository.countByUserIdAndTopicIdInAndCompletedAtBetween(
                    userId, topicIds, windowStart.atStartOfDay(java.time.ZoneOffset.UTC).toInstant(), Instant.now());
        }

        BigDecimal paceTopicsPerWeek = windowDays > 0
                ? BigDecimal.valueOf(completedInWindow).multiply(BigDecimal.valueOf(7))
                        .divide(BigDecimal.valueOf(windowDays), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int remainingTopics = Math.max(0, totalTopics - (int) completedTopics);
        LocalDate predictedCompletionDate = predictCompletionDate(today, remainingTopics, paceTopicsPerWeek);
        BigDecimal coveragePct = coveragePct(totalTopics, completedTopics);

        AdherenceReportResponse adherence = reportService.getAdherence(userId, windowStart, today, null);
        Confidence confidence = confidenceFor(windowDays, completedInWindow, adherence.totalLoggedDays());

        return new StatsSnapshot(null, "Overall", totalTopics, (int) completedTopics, coveragePct,
                paceTopicsPerWeek, predictedCompletionDate, confidence, adherence.adherencePct(), adherence.currentStreak());
    }

    private BigDecimal computePace(Long userId, Long trackId, List<Topic> topics, long completedInWindow,
                                    int windowDays, LocalDate windowStart, LocalDate today) {
        if (completedInWindow > 0 && windowDays > 0) {
            return BigDecimal.valueOf(completedInWindow).multiply(BigDecimal.valueOf(7))
                    .divide(BigDecimal.valueOf(windowDays), 2, RoundingMode.HALF_UP);
        }

        List<DailyTrackLog> logs = dailyTrackLogRepository
                .findByUserIdAndTrackIdAndLogDateBetweenOrderByLogDateAsc(userId, trackId, windowStart, today);
        if (logs.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalActualHours = logs.stream().map(DailyTrackLog::getActualHours).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgActualHoursPerDay = totalActualHours.divide(BigDecimal.valueOf(logs.size()), 4, RoundingMode.HALF_UP);
        if (avgActualHoursPerDay.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        double avgEstimatedMinutes = topics.stream()
                .mapToInt(t -> t.getEstimatedMinutes() == null ? DEFAULT_ESTIMATED_MINUTES : t.getEstimatedMinutes())
                .average().orElse(DEFAULT_ESTIMATED_MINUTES);

        return avgActualHoursPerDay.multiply(BigDecimal.valueOf(7 * 60))
                .divide(BigDecimal.valueOf(avgEstimatedMinutes), 2, RoundingMode.HALF_UP);
    }

    private LocalDate predictCompletionDate(LocalDate today, int remainingTopics, BigDecimal paceTopicsPerWeek) {
        if (remainingTopics <= 0) {
            return today;
        }
        if (paceTopicsPerWeek.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal weeksRemaining = BigDecimal.valueOf(remainingTopics).divide(paceTopicsPerWeek, 4, RoundingMode.HALF_UP);
        long daysRemaining = (long) Math.ceil(weeksRemaining.doubleValue() * 7);
        return today.plusDays(daysRemaining);
    }

    private BigDecimal coveragePct(int totalTopics, long completedTopics) {
        if (totalTopics == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(completedTopics).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalTopics), 2, RoundingMode.HALF_UP);
    }

    private Confidence confidenceFor(int windowDays, long completedInWindow, int totalLoggedDays) {
        if (windowDays >= 14 && completedInWindow >= 3) {
            return Confidence.HIGH;
        }
        if (totalLoggedDays >= 3 || completedInWindow > 0) {
            return Confidence.MEDIUM;
        }
        return Confidence.LOW;
    }

    private int windowDays(Instant trackCreatedAt, LocalDate today) {
        long daysSinceCreation = ChronoUnit.DAYS.between(
                trackCreatedAt.atZone(java.time.ZoneOffset.UTC).toLocalDate(), today);
        return (int) Math.max(1, Math.min(lookbackDays, Math.max(daysSinceCreation, 1)));
    }
}
