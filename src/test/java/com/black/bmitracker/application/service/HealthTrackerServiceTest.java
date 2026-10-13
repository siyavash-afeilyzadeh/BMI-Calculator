package com.black.bmitracker.application.service;

import com.black.bmitracker.application.dto.BmiResultDto;
import com.black.bmitracker.domain.model.entity.MeasurementEntry;
import com.black.bmitracker.domain.model.entity.UserProfile;
import com.black.bmitracker.domain.model.enums.BmiCategory;
import com.black.bmitracker.domain.model.enums.Gender;
import com.black.bmitracker.domain.model.enums.UnitSystem;
import com.black.bmitracker.infrastructure.persistence.config.HibernateUtil;
import com.black.bmitracker.infrastructure.persistence.repository.MeasurementEntryRepository;
import com.black.bmitracker.infrastructure.persistence.repository.UserProfileRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HealthTrackerServiceTest {

    private HealthTrackerService healthTrackerService;
    private UserProfileRepository userProfileRepository;
    private MeasurementEntryRepository measurementEntryRepository;

    @BeforeEach
    void setUp() {
        cleanupDatabase();
        healthTrackerService = new HealthTrackerService();
        userProfileRepository = new UserProfileRepository();
        measurementEntryRepository = new MeasurementEntryRepository();
    }

    private void cleanupDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM MeasurementEntry").executeUpdate();
            session.createMutationQuery("DELETE FROM UserProfile").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    @DisplayName("Should create new profile when no profile exists")
    void shouldCreateNewProfileWhenNoneExists() {
        UserProfile profile = healthTrackerService.getOrCreateDefaultProfile(
                "MrBlack", Gender.MALE, LocalDate.of(1990, 5, 20)
        );

        assertNotNull(profile);
        assertNotNull(profile.getId());
        assertEquals("MrBlack", profile.getUsername());
        assertEquals(Gender.MALE, profile.getGender());
        assertEquals(LocalDate.of(1990, 5, 20), profile.getBirthDate());
    }

    @Test
    @DisplayName("Should return existing profile without creating duplicates")
    void shouldReturnExistingProfileWhenAlreadyExists() {
        UserProfile initial = healthTrackerService.getOrCreateDefaultProfile(
                "MrBlack", Gender.MALE, LocalDate.of(1990, 5, 20)
        );

        UserProfile existing = healthTrackerService.getOrCreateDefaultProfile(
                "SecondaryUser", Gender.FEMALE, LocalDate.of(1995, 1, 1)
        );

        assertEquals(initial.getId(), existing.getId());
        assertEquals("MrBlack", existing.getUsername());
        assertEquals(1, userProfileRepository.findAll().size());
    }

    @Test
    @DisplayName("Should record measurement correctly using metric unit system")
    void shouldRecordMeasurementWithMetricUnits() {
        UserProfile profile = healthTrackerService.getOrCreateDefaultProfile(
                "MrBlack", Gender.MALE, LocalDate.of(1990, 5, 20)
        );

        double weight = 70.0;
        double height = 175.0;

        BmiResultDto result = healthTrackerService.recordMeasurement(
                profile, weight, height, UnitSystem.METRIC
        );

        assertNotNull(result);

        assertEquals(22.86, result.bmiValue(), 0.01);
        assertEquals(BmiCategory.NORMAL, result.category());
        assertEquals(UnitSystem.METRIC, result.unitSystem());


        List<MeasurementEntry> entries = measurementEntryRepository.findByUserProfileIdOrderByDateDesc(profile.getId());
        assertEquals(1, entries.size());
        MeasurementEntry savedEntry = entries.getFirst();
        assertEquals(70.0, savedEntry.getWeightKg(), 0.01);
        assertEquals(175.0, savedEntry.getHeightCm(), 0.01);
        assertEquals(22.86, savedEntry.getBmiValue(), 0.01);
        assertEquals(UnitSystem.METRIC, savedEntry.getRecordedUnitSystem());
    }

    @Test
    @DisplayName("Should record measurement correctly converting imperial units to metric for persistence")
    void shouldRecordMeasurementWithImperialUnits() {
        UserProfile profile = healthTrackerService.getOrCreateDefaultProfile(
                "MrBlack", Gender.MALE, LocalDate.of(1990, 5, 20)
        );

        double weightLbs = 154.32;
        double heightInches = 68.89;

        BmiResultDto result = healthTrackerService.recordMeasurement(
                profile, weightLbs, heightInches, UnitSystem.IMPERIAL
        );

        assertNotNull(result);

        List<MeasurementEntry> entries = measurementEntryRepository.findByUserProfileIdOrderByDateDesc(profile.getId());
        assertEquals(1, entries.size());
        MeasurementEntry savedEntry = entries.getFirst();

        // 154.32 lbs is approx 70.0 kg, 68.89 inches is approx 175.0 cm
        assertEquals(70.0, savedEntry.getWeightKg(), 0.1);
        assertEquals(175.0, savedEntry.getHeightCm(), 0.1);
        assertEquals(22.86, savedEntry.getBmiValue(), 0.1);
        assertEquals(UnitSystem.IMPERIAL, savedEntry.getRecordedUnitSystem());
    }

    @Test
    @DisplayName("Should retrieve measurement history and trend data in proper order")
    void shouldRetrieveMeasurementHistoryAndTrendData() {
        UserProfile profile = healthTrackerService.getOrCreateDefaultProfile(
                "MrBlack", Gender.MALE, LocalDate.of(1990, 5, 20)
        );

        healthTrackerService.recordMeasurement(profile, 75.0, 175.0, UnitSystem.METRIC);
        healthTrackerService.recordMeasurement(profile, 73.0, 175.0, UnitSystem.METRIC);
        healthTrackerService.recordMeasurement(profile, 70.0, 175.0, UnitSystem.METRIC);

        List<MeasurementEntry> history = healthTrackerService.getMeasurementHistory(profile.getId());
        List<MeasurementEntry> trend = healthTrackerService.getTrendData(profile.getId());

        assertEquals(3, history.size());
        assertEquals(3, trend.size());

        // Latest entry should be first in history (Descending)
        assertEquals(70.0, history.getFirst().getWeightKg(), 0.01);

        // Oldest entry should be first in trend (Ascending)
        assertEquals(75.0, trend.getFirst().getWeightKg(), 0.01);
    }
}
