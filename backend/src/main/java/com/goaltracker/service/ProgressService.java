package com.goaltracker.service;

import com.goaltracker.domain.TopicLog;
import com.goaltracker.domain.TopicProgress;
import com.goaltracker.domain.enums.TopicLogStatus;
import com.goaltracker.domain.enums.TopicProgressStatus;
import com.goaltracker.dto.log.TopicLogRequest;
import com.goaltracker.dto.log.TopicLogResponse;
import com.goaltracker.dto.topic.TopicProgressResponse;
import com.goaltracker.repository.TopicLogRepository;
import com.goaltracker.repository.TopicProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/** Sole writer of topic_log / topic_progress. Status on topic_progress only ever moves forward. */
@Service
public class ProgressService {

    private static final List<TopicProgressStatus> STATUS_ORDER = List.of(
            TopicProgressStatus.NOT_STARTED, TopicProgressStatus.STARTED,
            TopicProgressStatus.IN_PROGRESS, TopicProgressStatus.PRACTICED, TopicProgressStatus.COMPLETED);

    private final TopicLogRepository topicLogRepository;
    private final TopicProgressRepository topicProgressRepository;

    public ProgressService(TopicLogRepository topicLogRepository, TopicProgressRepository topicProgressRepository) {
        this.topicLogRepository = topicLogRepository;
        this.topicProgressRepository = topicProgressRepository;
    }

    @Transactional
    public TopicLogResponse recordTopicLog(Long userId, TopicLogRequest request) {
        TopicLogStatus logStatus = TopicLogStatus.valueOf(request.status().toUpperCase());

        TopicLog log = new TopicLog();
        log.setUserId(userId);
        log.setTopicId(request.topicId());
        log.setLogDate(request.date());
        log.setStatus(logStatus);
        log.setMinutesSpent(request.minutesSpent() == null ? 0 : request.minutesSpent());
        log.setNotes(request.notes());
        TopicLog saved = topicLogRepository.save(log);

        applyToProgress(userId, request.topicId(), logStatus, saved.getMinutesSpent());

        return new TopicLogResponse(saved.getId(), saved.getTopicId(), saved.getLogDate(),
                saved.getStatus().name(), saved.getMinutesSpent(), saved.getNotes());
    }

    public List<TopicLogResponse> listLogs(Long userId, Long topicId, LocalDate from, LocalDate to) {
        List<TopicLog> logs = (from != null && to != null)
                ? topicLogRepository.findByUserIdAndTopicIdAndLogDateBetweenOrderByLogDateAsc(userId, topicId, from, to)
                : topicLogRepository.findByUserIdAndTopicIdOrderByLogDateDesc(userId, topicId);
        return logs.stream()
                .map(l -> new TopicLogResponse(l.getId(), l.getTopicId(), l.getLogDate(), l.getStatus().name(),
                        l.getMinutesSpent(), l.getNotes()))
                .toList();
    }

    public TopicProgressResponse getProgress(Long userId, Long topicId) {
        return topicProgressRepository.findByUserIdAndTopicId(userId, topicId)
                .map(this::toResponse)
                .orElse(TopicProgressResponse.notStarted());
    }

    private void applyToProgress(Long userId, Long topicId, TopicLogStatus logStatus, int minutesSpent) {
        TopicProgress progress = topicProgressRepository.findByUserIdAndTopicId(userId, topicId)
                .orElseGet(() -> {
                    TopicProgress p = new TopicProgress();
                    p.setUserId(userId);
                    p.setTopicId(topicId);
                    return p;
                });

        progress.setTimesPracticed(progress.getTimesPracticed() + 1);
        progress.setTotalMinutesSpent(progress.getTotalMinutesSpent() + minutesSpent);
        progress.setLastPracticedAt(Instant.now());

        TopicProgressStatus candidate = TopicProgressStatus.valueOf(logStatus.name());
        if (STATUS_ORDER.indexOf(candidate) > STATUS_ORDER.indexOf(progress.getStatus())) {
            progress.setStatus(candidate);
            if (candidate == TopicProgressStatus.COMPLETED && progress.getCompletedAt() == null) {
                progress.setCompletedAt(Instant.now());
            }
        }

        topicProgressRepository.save(progress);
    }

    private TopicProgressResponse toResponse(TopicProgress progress) {
        return new TopicProgressResponse(progress.getStatus().name(), progress.getTimesPracticed(),
                progress.getTotalMinutesSpent(), progress.getLastPracticedAt(), progress.getCompletedAt());
    }
}
