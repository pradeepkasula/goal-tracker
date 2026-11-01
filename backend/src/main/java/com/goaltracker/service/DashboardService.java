package com.goaltracker.service;

import com.goaltracker.domain.Topic;
import com.goaltracker.domain.enums.TopicLogStatus;
import com.goaltracker.dto.dashboard.TodayViewResponse;
import com.goaltracker.dto.log.DailyLogResponse;
import com.goaltracker.repository.TopicLogRepository;
import com.goaltracker.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final DailyLogService dailyLogService;
    private final TopicRepository topicRepository;
    private final TopicLogRepository topicLogRepository;

    public DashboardService(DailyLogService dailyLogService, TopicRepository topicRepository,
                             TopicLogRepository topicLogRepository) {
        this.dailyLogService = dailyLogService;
        this.topicRepository = topicRepository;
        this.topicLogRepository = topicLogRepository;
    }

    public TodayViewResponse getToday(Long userId) {
        LocalDate today = LocalDate.now();
        List<DailyLogResponse> trackLogs = dailyLogService.getOrCreateForDate(userId, today);

        BigDecimal totalPlanned = trackLogs.stream().map(DailyLogResponse::plannedHours).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalActual = trackLogs.stream().map(DailyLogResponse::actualHours).reduce(BigDecimal.ZERO, BigDecimal::add);

        String dayType = trackLogs.isEmpty() ? null : trackLogs.get(0).dayType();

        List<Topic> allTopics = topicRepository.findByUserIdOrderBySortOrderAsc(userId);
        int touchedToday = 0;
        int completedToday = 0;
        for (Topic topic : allTopics) {
            var logsToday = topicLogRepository.findByUserIdAndTopicIdAndLogDateBetweenOrderByLogDateAsc(
                    userId, topic.getId(), today, today);
            if (!logsToday.isEmpty()) {
                touchedToday++;
                if (logsToday.stream().anyMatch(l -> l.getStatus() == TopicLogStatus.COMPLETED)) {
                    completedToday++;
                }
            }
        }

        return new TodayViewResponse(today, dayType, trackLogs, totalPlanned, totalActual, completedToday, touchedToday);
    }
}
