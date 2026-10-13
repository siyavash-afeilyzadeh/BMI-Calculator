package com.black.bmitracker.infrastructure.persistence.repository;

import com.black.bmitracker.domain.model.entity.MeasurementEntry;
import com.black.bmitracker.domain.repository.MeasurementEntryRepositoryPort;
import com.black.bmitracker.infrastructure.persistence.config.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class MeasurementEntryRepository
        extends AbstractHibernateRepository<MeasurementEntry, Long>
        implements MeasurementEntryRepositoryPort {

    public MeasurementEntryRepository() {
        super(MeasurementEntry.class);
    }

    @Override
    public List<MeasurementEntry> findByUserProfileIdOrderByDateAsc(Long userProfileId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM MeasurementEntry m WHERE m.userProfile.id = :profileId ORDER BY m.recordedAt ASC",
                            MeasurementEntry.class)
                    .setParameter("profileId", userProfileId)
                    .getResultList();
        }
    }

    @Override
    public List<MeasurementEntry> findByUserProfileIdOrderByDateDesc(Long userProfileId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM MeasurementEntry m WHERE m.userProfile.id = :profileId ORDER BY m.recordedAt DESC",
                            MeasurementEntry.class)
                    .setParameter("profileId", userProfileId)
                    .getResultList();
        }
    }
}
