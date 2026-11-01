package com.goaltracker.dto.holiday;

import java.time.LocalDate;

public record HolidayResponse(
        Long id,
        LocalDate date,
        String label
) {
}
