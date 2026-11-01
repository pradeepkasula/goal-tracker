package com.goaltracker.service;

import com.goaltracker.domain.Holiday;
import com.goaltracker.dto.holiday.HolidayRequest;
import com.goaltracker.dto.holiday.HolidayResponse;
import com.goaltracker.exception.ResourceNotFoundException;
import com.goaltracker.exception.ValidationException;
import com.goaltracker.repository.HolidayRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public List<HolidayResponse> listHolidays(Long userId, LocalDate from, LocalDate to) {
        return holidayRepository.findByUserIdAndHolidayDateBetweenOrderByHolidayDateAsc(userId, from, to).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public HolidayResponse addHoliday(Long userId, HolidayRequest request) {
        try {
            Holiday holiday = new Holiday();
            holiday.setUserId(userId);
            holiday.setHolidayDate(request.date());
            holiday.setLabel(request.label());
            return toResponse(holidayRepository.save(holiday));
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("A holiday is already recorded for " + request.date());
        }
    }

    @Transactional
    public void deleteHoliday(Long userId, Long holidayId) {
        Holiday holiday = holidayRepository.findByIdAndUserId(holidayId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found"));
        holidayRepository.delete(holiday);
    }

    private HolidayResponse toResponse(Holiday holiday) {
        return new HolidayResponse(holiday.getId(), holiday.getHolidayDate(), holiday.getLabel());
    }
}
