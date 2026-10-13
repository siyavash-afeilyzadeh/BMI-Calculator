package com.black.bmitracker.infrastructure.persistence.repository;

import com.black.bmitracker.infrastructure.persistence.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public abstract class AbstractHibernateRepository<T, ID> implements BaseRepository<T, ID> {

    private final Class<T> entityClass;

    protected AbstractHibernateRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Failed to persist entity: " + entityClass.getSimpleName(), ex);
        }
    }

    @Override
    public T update(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            T merged = session.merge(entity);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Failed to update entity: " + entityClass.getSimpleName(), ex);
        }
    }

    @Override
    public void delete(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(session.contains(entity) ? entity : session.merge(entity));
            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Failed to delete entity: " + entityClass.getSimpleName(), ex);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(entityClass, id));
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM " + entityClass.getSimpleName();
            return session.createQuery(hql, entityClass).getResultList();
        }
    }
}
