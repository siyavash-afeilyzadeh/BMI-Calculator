package com.black.bmitracker.infrastructure.persistence.repository;

import com.black.bmitracker.domain.model.entity.UserProfile;
import com.black.bmitracker.domain.model.enums.Gender;
import com.black.bmitracker.infrastructure.persistence.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserProfileRepositoryTest {

    private UserProfileRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UserProfileRepository();
        cleanupDatabase();
    }

    private void cleanupDatabase() {
        List<UserProfile> allUsers = repository.findAll();
        for (UserProfile user : allUsers) {
            repository.delete(user);
        }
    }

    @Test
    @DisplayName("Should persist UserProfile and generate an ID")
    void shouldPersistUserProfileSuccessfully() {
        UserProfile user = UserProfile.builder()
                .username("mr_black")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 5, 20))
                .build();

        UserProfile saved = repository.save(user);

        assertNotNull(saved.getId());
        Optional<UserProfile> retrieved = repository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("mr_black", retrieved.get().getUsername());
        assertEquals(Gender.MALE, retrieved.get().getGender());
    }

    @Test
    @DisplayName("Should find UserProfile by username")
    void shouldFindUserProfileByUsername() {
        UserProfile user = UserProfile.builder()
                .username("unique_user")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1995, 10, 15))
                .build();
        repository.save(user);

        Optional<UserProfile> found = repository.findByUsername("unique_user");

        assertTrue(found.isPresent());
        assertEquals("unique_user", found.get().getUsername());
    }

    @Test
    @DisplayName("Should find the first profile ordered by ID ascending")
    void shouldFindFirstProfile() {
        UserProfile user1 = UserProfile.builder()
                .username("first_user")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1988, 1, 1))
                .build();
        UserProfile user2 = UserProfile.builder()
                .username("second_user")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1992, 2, 2))
                .build();

        repository.save(user1);
        repository.save(user2);

        Optional<UserProfile> first = repository.findFirstProfile();

        assertTrue(first.isPresent());
        assertEquals("first_user", first.get().getUsername());
    }

    @Test
    @DisplayName("Should delete UserProfile successfully")
    void shouldDeleteUserProfile() {
        UserProfile user = UserProfile.builder()
                .username("to_be_deleted")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(2000, 3, 12))
                .build();
        UserProfile saved = repository.save(user);

        repository.delete(saved);

        Optional<UserProfile> found = repository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}
