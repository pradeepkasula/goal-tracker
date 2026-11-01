package com.goaltracker.repository;

import com.goaltracker.domain.DailyTrackLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyTrackLogRepository extends JpaRepository<DailyTrackLog, Long> {
    Optional<DailyTrackLog> findByUserIdAndTrackIdAndLogDate(Long userId, Long trackId, LocalDate logDate);
    List<DailyTrackLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);
    List<DailyTrackLog> findByUserIdAndLogDateBetweenOrderByLogDateAsc(Long userId, LocalDate from, LocalDate to);
    List<DailyTrackLog> findByUserIdAndTrackIdAndLogDateBetweenOrderByLogDateAsc(Long userId, Long trackId, LocalDate from, LocalDate to);
}
