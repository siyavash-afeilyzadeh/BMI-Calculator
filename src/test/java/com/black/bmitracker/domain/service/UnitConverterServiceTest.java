package com.black.bmitracker.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnitConverterServiceTest {

    private UnitConverterService unitConverterService;

    @BeforeEach
    void setUp() {
        unitConverterService = new UnitConverterService();
    }

    @Test
    @DisplayName("Should convert kilograms to pounds rounded to two decimal places")
    void shouldConvertKgToLbs() {
        double result = unitConverterService.kgToLbs(70.0);
        assertEquals(154.32, result, 0.01);
    }

    @Test
    @DisplayName("Should convert pounds to kilograms rounded to two decimal places")
    void shouldConvertLbsToKg() {
        double result = unitConverterService.lbsToKg(154.32);
        assertEquals(70.0, result, 0.01);
    }

    @Test
    @DisplayName("Should convert centimeters to inches rounded to two decimal places")
    void shouldConvertCmToInches() {
        double result = unitConverterService.cmToInches(175.0);
        assertEquals(68.90, result, 0.01);
    }

    @Test
    @DisplayName("Should convert inches to centimeters rounded to two decimal places")
    void shouldConvertInchesToCm() {
        double result = unitConverterService.inchesToCm(68.90);
        assertEquals(175.01, result, 0.02);
    }
}
