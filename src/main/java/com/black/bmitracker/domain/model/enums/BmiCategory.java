package com.black.bmitracker.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BmiCategory {
    UNDERWEIGHT("Underweight"),
    NORMAL("Normal weight"),
    OVERWEIGHT("Overweight"),
    OBESE_CLASS_I("Obesity Class I"),
    OBESE_CLASS_II("Obesity Class II"),
    OBESE_CLASS_III("Obesity Class III");

    private final String displayName;
}
