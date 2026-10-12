package com.black.bmitracker.domain.service;

import com.black.bmitracker.domain.exception.InvalidMeasurementException;
import com.black.bmitracker.domain.model.enums.UnitSystem;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BmiCalculatorService {

    public double calculate(double weight, double height, UnitSystem unitSystem) {
        validateInputs(weight, height);

        double bmi = switch (unitSystem) {
            case METRIC -> calculateMetric(weight, height);
            case IMPERIAL -> calculateImperial(weight, height);
        };

        return round(bmi, 2);
    }

    private double calculateMetric(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    private double calculateImperial(double weightLbs, double heightInches) {
        return (weightLbs / (heightInches * heightInches)) * 703.0;
    }

    private void validateInputs(double weight, double height) {
        if (weight <= 0) {
            throw new InvalidMeasurementException("Weight must be greater than zero.");
        }
        if (height <= 0) {
            throw new InvalidMeasurementException("Height must be greater than zero.");
        }
    }

    private double round(double value, int places) {
        return BigDecimal.valueOf(value)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
