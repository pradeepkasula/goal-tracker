package com.goaltracker.dto.holiday;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record HolidayRequest(
        @NotNull LocalDate date,
        String label
) {
}
