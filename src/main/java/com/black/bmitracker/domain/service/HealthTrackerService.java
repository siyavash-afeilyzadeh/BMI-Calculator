package com.black.bmitracker.domain.service;

import com.black.bmitracker.domain.dto.BmiResultDto;
import com.black.bmitracker.domain.model.MeasurementEntry;
import com.black.bmitracker.domain.model.UserProfile;
import com.black.bmitracker.domain.model.enums.BmiCategory;
import com.black.bmitracker.domain.model.enums.Gender;
import com.black.bmitracker.domain.model.enums.UnitSystem;
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

    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    public UserProfile getOrCreateProfileByUsername(String username, Gender gender, LocalDate birthDate) {
        return userProfileRepository.findByUsername(username)
                .orElseGet(() -> userProfileRepository.save(
                        UserProfile.builder()
                                .username(username)
                                .gender(gender != null ? gender : Gender.MALE)
                                .birthDate(birthDate != null ? birthDate : LocalDate.now().minusYears(25))
                                .build()
                ));
    }

    public UserProfile getOrCreateDefaultProfile(String username, Gender gender, LocalDate birthDate) {
        return userProfileRepository.findFirstProfile()
                .orElseGet(() -> userProfileRepository.save(
                        UserProfile.builder()
                                .username(username)
                                .gender(gender)
                                .birthDate(birthDate)
                                .build()
                ));
    }

    public BmiResultDto recordMeasurement(UserProfile profile, double weight, double height, UnitSystem unitSystem) {
        return recordMeasurement(profile, weight, height, unitSystem, LocalDateTime.now());
    }

    public BmiResultDto recordMeasurement(UserProfile profile, double weight, double height, UnitSystem unitSystem, LocalDateTime recordedAt) {
        double bmi = bmiCalculatorService.calculate(weight, height, unitSystem);
        BmiCategory category = categoryClassifier.classify(bmi);

        double metricWeight = (unitSystem == UnitSystem.METRIC) ? weight : unitConverterService.lbsToKg(weight);
        double metricHeight = (unitSystem == UnitSystem.METRIC) ? height : unitConverterService.inchesToCm(height);

        MeasurementEntry entry = MeasurementEntry.builder()
                .userProfile(profile)
                .heightCm(metricHeight)
                .weightKg(metricWeight)
                .bmiValue(bmi)
                .recordedUnitSystem(unitSystem)
                .recordedAt(recordedAt != null ? recordedAt : LocalDateTime.now())
                .build();

        MeasurementEntry savedEntry = measurementEntryRepository.save(entry);

        return new BmiResultDto(
                savedEntry.getId(),
                weight,
                height,
                savedEntry.getBmiValue(),
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
