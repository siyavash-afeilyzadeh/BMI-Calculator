package com.black.bmitracker.domain.repository;

import com.black.bmitracker.domain.model.entity.UserProfile;
import java.util.List;
import java.util.Optional;

public interface UserProfileRepositoryPort {
    UserProfile save(UserProfile userProfile);
    UserProfile update(UserProfile userProfile);
    void delete(UserProfile userProfile);
    Optional<UserProfile> findById(Long id);
    List<UserProfile> findAll();
    Optional<UserProfile> findByUsername(String username);
    Optional<UserProfile> findFirstProfile();
    void deleteById(Long id);
}
