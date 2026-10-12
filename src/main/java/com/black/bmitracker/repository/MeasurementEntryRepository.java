package com.black.bmitracker.repository;

import com.black.bmitracker.domain.model.MeasurementEntry;
import org.hibernate.Session;

import java.util.List;

public class MeasurementEntryRepository extends AbstractHibernateRepository<MeasurementEntry, Long> {

    public MeasurementEntryRepository() {
        super(MeasurementEntry.class);
    }

    public List<MeasurementEntry> findByUserProfileIdOrderByDateAsc(Long userProfileId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM MeasurementEntry m WHERE m.userProfile.id = :profileId ORDER BY m.recordedAt ASC",
                            MeasurementEntry.class
                    )
                    .setParameter("profileId", userProfileId)
                    .getResultList();
        }
    }

    public List<MeasurementEntry> findByUserProfileIdOrderByDateDesc(Long userProfileId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM MeasurementEntry m WHERE m.userProfile.id = :profileId ORDER BY m.recordedAt DESC",
                            MeasurementEntry.class
                    )
                    .setParameter("profileId", userProfileId)
                    .getResultList();
        }
    }
}
