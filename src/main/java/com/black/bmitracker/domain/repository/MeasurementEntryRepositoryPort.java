package com.black.bmitracker.domain.repository;

import com.black.bmitracker.domain.model.entity.MeasurementEntry;
import java.util.List;
import java.util.Optional;

public interface MeasurementEntryRepositoryPort {
    MeasurementEntry save(MeasurementEntry entry);
    MeasurementEntry update(MeasurementEntry entry);
    void delete(MeasurementEntry entry);
    Optional<MeasurementEntry> findById(Long id);
    List<MeasurementEntry> findAll();
    List<MeasurementEntry> findByUserProfileIdOrderByDateAsc(Long userProfileId);
    List<MeasurementEntry> findByUserProfileIdOrderByDateDesc(Long userProfileId);
}
