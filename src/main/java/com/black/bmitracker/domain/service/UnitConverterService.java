package com.black.bmitracker.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitConverterService {

    public static final double KG_TO_LBS = 2.20462262185;
    public static final double CM_TO_INCH = 0.3937007874;

    public double kgToLbs(double kg) {
        return round(kg * KG_TO_LBS, 2);
    }

    public double lbsToKg(double lbs) {
        return round(lbs / KG_TO_LBS, 2);
    }

    public double cmToInches(double cm) {
        return round(cm * CM_TO_INCH, 2);
    }

    public double inchesToCm(double inches) {
        return round(inches / CM_TO_INCH, 2);
    }

    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException("Decimal places must not be negative");
        }
        return BigDecimal.valueOf(value)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
