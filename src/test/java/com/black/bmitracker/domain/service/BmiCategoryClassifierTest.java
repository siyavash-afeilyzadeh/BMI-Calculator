package com.black.bmitracker.domain.service;

import com.black.bmitracker.domain.model.enums.BmiCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BmiCategoryClassifierTest {

    private BmiCategoryClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new BmiCategoryClassifier();
    }

    @Test
    @DisplayName("Should classify BMI value into correct category enum")
    void shouldClassifyBmiCorrectly() {
        assertEquals(BmiCategory.UNDERWEIGHT, classifier.classify(17.5));
        assertEquals(BmiCategory.NORMAL, classifier.classify(22.5));
        assertEquals(BmiCategory.OVERWEIGHT, classifier.classify(27.8));
        assertEquals(BmiCategory.OBESE_CLASS_I, classifier.classify(32.4));
        assertEquals(BmiCategory.OBESE_CLASS_II, classifier.classify(37.1));
        assertEquals(BmiCategory.OBESE_CLASS_III, classifier.classify(42.5));
    }

    @Test
    @DisplayName("Should return expected display name for each BMI category")
    void shouldReturnCorrectDisplayNames() {
        assertEquals("Underweight", classifier.getDisplayName(BmiCategory.UNDERWEIGHT));
        assertEquals("Normal weight", classifier.getDisplayName(BmiCategory.NORMAL));
        assertEquals("Overweight", classifier.getDisplayName(BmiCategory.OVERWEIGHT));
        assertEquals("Obesity Class I", classifier.getDisplayName(BmiCategory.OBESE_CLASS_I));
        assertEquals("Obesity Class II", classifier.getDisplayName(BmiCategory.OBESE_CLASS_II));
        assertEquals("Obesity Class III", classifier.getDisplayName(BmiCategory.OBESE_CLASS_III));
    }
}
