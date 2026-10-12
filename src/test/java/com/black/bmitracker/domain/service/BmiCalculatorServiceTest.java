package com.black.bmitracker.domain.service;

import com.black.bmitracker.domain.exception.InvalidMeasurementException;
import com.black.bmitracker.domain.model.enums.UnitSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BmiCalculatorServiceTest {

    private BmiCalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new BmiCalculatorService();
    }

    @Test
    @DisplayName("Should correctly calculate BMI using metric unit system")
    void shouldCalculateBmiForMetricSystem() {
        double weightKg = 70.0;
        double heightCm = 175.0;

        double bmi = calculatorService.calculate(weightKg, heightCm, UnitSystem.METRIC);

        assertEquals(22.86, bmi, 0.01);
    }

    @Test
    @DisplayName("Should correctly calculate BMI using imperial unit system")
    void shouldCalculateBmiForImperialSystem() {
        double weightLbs = 154.32;
        double heightInches = 68.90;

        double bmi = calculatorService.calculate(weightLbs, heightInches, UnitSystem.IMPERIAL);

        assertEquals(22.87, bmi, 0.05);
    }

    @Test
    @DisplayName("Should throw InvalidMeasurementException when weight is zero or negative")
    void shouldThrowExceptionWhenWeightIsZeroOrNegative() {
        assertThrows(InvalidMeasurementException.class, () ->
                calculatorService.calculate(0.0, 180.0, UnitSystem.METRIC));

        assertThrows(InvalidMeasurementException.class, () ->
                calculatorService.calculate(-5.0, 180.0, UnitSystem.METRIC));
    }

    @Test
    @DisplayName("Should throw InvalidMeasurementException when height is zero or negative")
    void shouldThrowExceptionWhenHeightIsZeroOrNegative() {
        assertThrows(InvalidMeasurementException.class, () ->
                calculatorService.calculate(70.0, 0.0, UnitSystem.METRIC));

        assertThrows(InvalidMeasurementException.class, () ->
                calculatorService.calculate(70.0, -170.0, UnitSystem.METRIC));
    }
}
