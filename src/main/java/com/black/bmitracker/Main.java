package com.black.bmitracker;

import com.black.bmitracker.repository.HibernateUtil;
import org.hibernate.Session;

class Main {
    static void main() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (session.isOpen()) {
                System.out.println("Hibernate Session opened. Tables created and verified successfully!");
            }
        } catch (Exception ex) {
            System.err.println("Database verification error: " + ex.getMessage());
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
