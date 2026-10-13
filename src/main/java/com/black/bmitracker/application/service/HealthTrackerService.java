package com.black.bmitracker.application.service;

import com.black.bmitracker.application.dto.BmiResultDto;
import com.black.bmitracker.domain.model.entity.MeasurementEntry;
import com.black.bmitracker.domain.model.entity.UserProfile;
import com.black.bmitracker.domain.model.enums.BmiCategory;
import com.black.bmitracker.domain.model.enums.Gender;
import com.black.bmitracker.domain.model.enums.UnitSystem;
import com.black.bmitracker.domain.repository.MeasurementEntryRepositoryPort;
import com.black.bmitracker.domain.repository.UserProfileRepositoryPort;
import com.black.bmitracker.domain.service.BmiCalculatorService;
import com.black.bmitracker.domain.service.BmiCategoryClassifier;
import com.black.bmitracker.domain.service.UnitConverterService;
import com.black.bmitracker.infrastructure.persistence.repository.MeasurementEntryRepository;
import com.black.bmitracker.infrastructure.persistence.repository.UserProfileRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates business logic, user profile persistence, and BMI measurements.
 * Adheres strictly to Clean Architecture and DIP using repository ports.
 */
public class HealthTrackerService {

    private final UserProfileRepositoryPort userProfileRepository;
    private final MeasurementEntryRepositoryPort measurementEntryRepository;
    private final BmiCalculatorService bmiCalculatorService;
    private final BmiCategoryClassifier bmiCategoryClassifier;
    private final UnitConverterService unitConverterService;

    public HealthTrackerService() {
        this(
                new UserProfileRepository(),
                new MeasurementEntryRepository(),
                new BmiCalculatorService(),
                new BmiCategoryClassifier(),
                new UnitConverterService()
        );
    }

    public HealthTrackerService(
            UserProfileRepositoryPort userProfileRepository,
            MeasurementEntryRepositoryPort measurementEntryRepository,
            BmiCalculatorService bmiCalculatorService,
            BmiCategoryClassifier bmiCategoryClassifier,
            UnitConverterService unitConverterService
    ) {
        this.userProfileRepository = Objects.requireNonNull(userProfileRepository, "userProfileRepository cannot be null");
        this.measurementEntryRepository = Objects.requireNonNull(measurementEntryRepository, "measurementEntryRepository cannot be null");
        this.bmiCalculatorService = Objects.requireNonNull(bmiCalculatorService, "bmiCalculatorService cannot be null");
        this.bmiCategoryClassifier = Objects.requireNonNull(bmiCategoryClassifier, "bmiCategoryClassifier cannot be null");
        this.unitConverterService = Objects.requireNonNull(unitConverterService, "unitConverterService cannot be null");
    }

    // =========================================================================
    // Profile Management
    // =========================================================================

    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    public UserProfile getOrCreateProfileByUsername(String username, Gender gender, LocalDate birthDate) {
        return userProfileRepository.findByUsername(username)
                .orElseGet(() -> {
                    UserProfile profile = UserProfile.builder()
                            .username(username)
                            .gender(gender)
                            .birthDate(birthDate)
                            .build();
                    return userProfileRepository.save(profile);
                });
    }

    public UserProfile getOrCreateDefaultProfile(String username, Gender gender, LocalDate birthDate) {
        return userProfileRepository.findFirstProfile()
                .orElseGet(() -> {
                    UserProfile profile = UserProfile.builder()
                            .username(username)
                            .gender(gender)
                            .birthDate(birthDate)
                            .build();
                    return userProfileRepository.save(profile);
                });
    }

    // =========================================================================
    // Measurement Recording & BMI Calculation
    // =========================================================================

    public BmiResultDto recordMeasurement(
            UserProfile profile,
            double weight,
            double height,
            UnitSystem unitSystem,
            LocalDateTime recordedAt
    ) {
        Objects.requireNonNull(profile, "UserProfile cannot be null");

        double calculatedBmi = bmiCalculatorService.calculate(weight, height, unitSystem);
        BmiCategory category = bmiCategoryClassifier.classify(calculatedBmi);

        double normalizedWeightKg = (unitSystem == UnitSystem.IMPERIAL)
                ? unitConverterService.lbsToKg(weight)
                : weight;

        double normalizedHeightCm = (unitSystem == UnitSystem.IMPERIAL)
                ? unitConverterService.inchesToCm(height)
                : height;

        LocalDateTime timestamp = (recordedAt != null) ? recordedAt : LocalDateTime.now();

        MeasurementEntry entry = MeasurementEntry.builder()
                .userProfile(profile)
                .weightKg(normalizedWeightKg)
                .heightCm(normalizedHeightCm)
                .bmiValue(calculatedBmi)
                .recordedUnitSystem(unitSystem)
                .recordedAt(timestamp)
                .build();

        MeasurementEntry saved = measurementEntryRepository.save(entry);

        return new BmiResultDto(
                saved.getId(),
                weight,
                height,
                calculatedBmi,
                category,
                unitSystem,
                saved.getRecordedAt()
        );
    }

    public BmiResultDto recordMeasurement(UserProfile profile, double weight, double height, UnitSystem unitSystem) {
        return recordMeasurement(profile, weight, height, unitSystem, LocalDateTime.now());
    }

    public BmiResultDto recordMeasurement(
            Long profileId,
            double weight,
            double height,
            UnitSystem unitSystem,
            LocalDateTime recordedAt
    ) {
        UserProfile profile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + profileId));
        return recordMeasurement(profile, weight, height, unitSystem, recordedAt);
    }

    public BmiResultDto recordMeasurement(Long profileId, double weight, double height, UnitSystem unitSystem) {
        return recordMeasurement(profileId, weight, height, unitSystem, LocalDateTime.now());
    }

    // =========================================================================
    // History & Trend Data
    // =========================================================================

    public List<MeasurementEntry> getMeasurementHistory(Long profileId) {
        return measurementEntryRepository.findByUserProfileIdOrderByDateDesc(profileId);
    }

    public List<MeasurementEntry> getTrendData(Long profileId) {
        return measurementEntryRepository.findByUserProfileIdOrderByDateAsc(profileId);
    }
}