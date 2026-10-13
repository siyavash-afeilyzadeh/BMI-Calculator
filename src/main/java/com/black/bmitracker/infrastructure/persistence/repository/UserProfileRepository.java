package com.black.bmitracker.infrastructure.persistence.repository;

import com.black.bmitracker.domain.model.entity.UserProfile;
import com.black.bmitracker.domain.repository.UserProfileRepositoryPort;
import com.black.bmitracker.infrastructure.persistence.config.HibernateUtil;
import org.hibernate.Session;

import java.util.Optional;

public class UserProfileRepository
        extends AbstractHibernateRepository<UserProfile, Long>
        implements UserProfileRepositoryPort {

    public UserProfileRepository() {
        super(UserProfile.class);
    }

    @Override
    public Optional<UserProfile> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM UserProfile u WHERE u.username = :username", UserProfile.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }

    @Override
    public Optional<UserProfile> findFirstProfile() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM UserProfile u ORDER BY u.id ASC", UserProfile.class)
                    .setMaxResults(1)
                    .uniqueResultOptional();
        }
    }
}
