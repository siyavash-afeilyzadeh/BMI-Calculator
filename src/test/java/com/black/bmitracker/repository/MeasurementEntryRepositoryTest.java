package com.black.bmitracker.repository;

import com.black.bmitracker.domain.model.MeasurementEntry;
import com.black.bmitracker.domain.model.UserProfile;
import com.black.bmitracker.domain.model.enums.Gender;
import com.black.bmitracker.domain.model.enums.UnitSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasurementEntryRepositoryTest {

    private MeasurementEntryRepository measurementRepository;
    private UserProfileRepository userRepository;
    private UserProfile testUser;

    @BeforeEach
    void setUp() {
        measurementRepository = new MeasurementEntryRepository();
        userRepository = new UserProfileRepository();

        List<UserProfile> allUsers = userRepository.findAll();
        for (UserProfile u : allUsers) {
            userRepository.delete(u);
        }

        UserProfile user = UserProfile.builder()
                .username("measurement_tester")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1993, 7, 24))
                .build();
        testUser = userRepository.save(user);
    }

    @Test
    @DisplayName("Should persist MeasurementEntry associated with UserProfile")
    void shouldPersistMeasurementEntry() {
        MeasurementEntry entry = MeasurementEntry.builder()
                .userProfile(testUser)
                .heightCm(180.0)
                .weightKg(75.5)
                .bmiValue(23.3)
                .recordedUnitSystem(UnitSystem.METRIC)
                .recordedAt(LocalDateTime.now())
                .build();

        MeasurementEntry saved = measurementRepository.save(entry);

        assertNotNull(saved.getId());
        Optional<MeasurementEntry> retrieved = measurementRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(75.5, retrieved.get().getWeightKg(), 0.001);
        assertEquals(testUser.getId(), retrieved.get().getUserProfile().getId());
    }

    @Test
    @DisplayName("Should retrieve entries ordered by recordedAt ascending and descending")
    void shouldRetrieveEntriesOrderedByDate() {
        LocalDateTime baseTime = LocalDateTime.of(2026, 1, 1, 10, 0);

        MeasurementEntry earlierEntry = MeasurementEntry.builder()
                .userProfile(testUser)
                .heightCm(180.0)
                .weightKg(80.0)
                .bmiValue(24.69)
                .recordedUnitSystem(UnitSystem.METRIC)
                .recordedAt(baseTime.minusDays(5))
                .build();

        MeasurementEntry laterEntry = MeasurementEntry.builder()
                .userProfile(testUser)
                .heightCm(180.0)
                .weightKg(78.0)
                .bmiValue(24.07)
                .recordedUnitSystem(UnitSystem.METRIC)
                .recordedAt(baseTime)
                .build();

        measurementRepository.save(earlierEntry);
        measurementRepository.save(laterEntry);

        List<MeasurementEntry> ascList = measurementRepository.findByUserProfileIdOrderByDateAsc(testUser.getId());
        assertEquals(2, ascList.size());
        assertEquals(80.0, ascList.get(0).getWeightKg(), 0.001);
        assertEquals(78.0, ascList.get(1).getWeightKg(), 0.001);

        List<MeasurementEntry> descList = measurementRepository.findByUserProfileIdOrderByDateDesc(testUser.getId());
        assertEquals(2, descList.size());
        assertEquals(78.0, descList.get(0).getWeightKg(), 0.001);
        assertEquals(80.0, descList.get(1).getWeightKg(), 0.001);
    }
}
