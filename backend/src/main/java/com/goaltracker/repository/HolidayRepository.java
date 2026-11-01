package com.goaltracker.repository;

import com.goaltracker.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    Optional<Holiday> findByUserIdAndHolidayDate(Long userId, LocalDate holidayDate);
    List<Holiday> findByUserIdAndHolidayDateBetweenOrderByHolidayDateAsc(Long userId, LocalDate from, LocalDate to);
    Optional<Holiday> findByIdAndUserId(Long id, Long userId);
}
