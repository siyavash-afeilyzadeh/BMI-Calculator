package com.black.bmitracker.repository;

import com.black.bmitracker.domain.model.UserProfile;
import org.hibernate.Session;

import java.util.Optional;

public class UserProfileRepository extends AbstractHibernateRepository<UserProfile, Long> {

    public UserProfileRepository() {
        super(UserProfile.class);
    }

    public Optional<UserProfile> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM UserProfile u WHERE u.username = :username", UserProfile.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }

    public Optional<UserProfile> findFirstProfile() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM UserProfile u ORDER BY u.id ASC", UserProfile.class)
                    .setMaxResults(1)
                    .uniqueResultOptional();
        }
    }
}
