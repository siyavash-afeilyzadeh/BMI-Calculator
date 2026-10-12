package com.black.bmitracker.domain.dto;

import com.black.bmitracker.domain.model.enums.BmiCategory;
import com.black.bmitracker.domain.model.enums.UnitSystem;

import java.time.LocalDateTime;

public record BmiResultDto(
        Long measurementId,
        double weight,
        double height,
        double bmiValue,
        BmiCategory category,
        UnitSystem unitSystem,
        LocalDateTime recordedAt
) {
}