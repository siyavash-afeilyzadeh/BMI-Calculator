package com.black.bmitracker.domain.service;

import com.black.bmitracker.domain.model.MeasurementEntry;
import com.black.bmitracker.domain.model.UserProfile;
import com.black.bmitracker.domain.model.enums.BmiCategory;
import com.black.bmitracker.domain.model.enums.Gender;
import com.black.bmitracker.domain.model.enums.UnitSystem;
import com.black.bmitracker.domain.dto.BmiResultDto;
import com.black.bmitracker.repository.MeasurementEntryRepository;
import com.black.bmitracker.repository.UserProfileRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HealthTrackerService {

    private final UserProfileRepository userProfileRepository;
    private final MeasurementEntryRepository measurementEntryRepository;
    private final BmiCalculatorService bmiCalculatorService;
    private final BmiCategoryClassifier categoryClassifier;
    private final UnitConverterService unitConverterService;

    public HealthTrackerService() {
        this.userProfileRepository = new UserProfileRepository();
        this.measurementEntryRepository = new MeasurementEntryRepository();
        this.bmiCalculatorService = new BmiCalculatorService();
        this.categoryClassifier = new BmiCategoryClassifier();
        this.unitConverterService = new UnitConverterService();
    }

    public UserProfile getOrCreateDefaultProfile(String username, Gender gender, LocalDate birthDate) {
        return userProfileRepository.findFirstProfile()
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.builder()
                            .username(username)
                            .gender(gender)
                            .birthDate(birthDate)
                            .build();
                    return userProfileRepository.save(newProfile);
                });
    }

    public BmiResultDto recordMeasurement(UserProfile profile, double weight, double height, UnitSystem unitSystem) {
        // ۱. محاسبه مقدار عددی BMI بر مبنای واحد وارد شده
        double bmiValue = bmiCalculatorService.calculate(weight, height, unitSystem);
        BmiCategory category = categoryClassifier.classify(bmiValue);

        // ۲. نرمال‌سازی قد و وزن به سیستم استاندارد متریک (cm و kg) برای ذخیره یکپارچه در دیتابیس
        double heightInCm = (unitSystem == UnitSystem.METRIC) ? height : unitConverterService.inchesToCm(height);
        double weightInKg = (unitSystem == UnitSystem.METRIC) ? weight : unitConverterService.lbsToKg(weight);

        // ۳. ذخیره در دیتابیس
        MeasurementEntry entry = MeasurementEntry.builder()
                .userProfile(profile)
                .heightCm(heightInCm)
                .weightKg(weightInKg)
                .bmiValue(bmiValue)
                .recordedUnitSystem(unitSystem)
                .recordedAt(LocalDateTime.now())
                .build();

        MeasurementEntry savedEntry = measurementEntryRepository.save(entry);

        return new BmiResultDto(
                savedEntry.getId(),
                weight,
                height,
                bmiValue,
                category,
                unitSystem,
                savedEntry.getRecordedAt()
        );
    }

    public List<MeasurementEntry> getMeasurementHistory(Long userProfileId) {
        return measurementEntryRepository.findByUserProfileIdOrderByDateDesc(userProfileId);
    }

    public List<MeasurementEntry> getTrendData(Long userProfileId) {
        return measurementEntryRepository.findByUserProfileIdOrderByDateAsc(userProfileId);
    }
}
