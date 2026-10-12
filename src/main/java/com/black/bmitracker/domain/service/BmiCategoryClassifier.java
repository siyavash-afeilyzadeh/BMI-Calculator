package com.black.bmitracker.domain.service;

import com.black.bmitracker.domain.model.enums.BmiCategory;

public class BmiCategoryClassifier {

    public BmiCategory classify(double bmi) {
        if (bmi < 18.5) {
            return BmiCategory.UNDERWEIGHT;
        } else if (bmi < 25.0) {
            return BmiCategory.NORMAL;
        } else if (bmi < 30.0) {
            return BmiCategory.OVERWEIGHT;
        } else if (bmi < 35.0) {
            return BmiCategory.OBESE_CLASS_I;
        } else if (bmi < 40.0) {
            return BmiCategory.OBESE_CLASS_II;
        } else {
            return BmiCategory.OBESE_CLASS_III;
        }
    }

    public String getDisplayName(BmiCategory category) {
        return switch (category) {
            case UNDERWEIGHT -> "Underweight";
            case NORMAL -> "Normal weight";
            case OVERWEIGHT -> "Overweight";
            case OBESE_CLASS_I -> "Obesity Class I";
            case OBESE_CLASS_II -> "Obesity Class II";
            case OBESE_CLASS_III -> "Obesity Class III";
        };
    }
}
