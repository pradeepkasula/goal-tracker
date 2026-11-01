package com.goaltracker.service;

import com.goaltracker.domain.enums.DayType;
import com.goaltracker.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class DayTypeResolver {

    private final HolidayRepository holidayRepository;

    public DayTypeResolver(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public DayType resolve(Long userId, LocalDate date) {
        if (holidayRepository.findByUserIdAndHolidayDate(userId, date).isPresent()) {
            return DayType.HOLIDAY;
        }
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return DayType.WEEKEND;
        }
        return DayType.WEEKDAY;
    }
}
