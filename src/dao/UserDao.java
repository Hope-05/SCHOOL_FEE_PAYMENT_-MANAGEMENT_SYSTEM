/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.user;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import util.HibernateUtil;

/**
 *
 * @author shema
 */
public class UserDao implements GenericDao<user, Long> {

    @Override
    public void save(user entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public user findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return (user) session.get(user.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<user> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM user");
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(user entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(user entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // --- Custom HQL Queries (Parameterized) ---

    // 1. Find a user by their username (Useful for Login)
    public user findByUsername(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM user u WHERE u.username = :username";
            Query query = session.createQuery(hql);
            query.setParameter("username", username);
            return (user) query.uniqueResult(); // uniqueResult because usernames are unique
        } finally {
            session.close();
        }
    }
    
    // 2. Check if a username already exists (Useful for registration)
    public boolean usernameExists(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "SELECT COUNT(u) FROM user u WHERE u.username = :username";
            Query query = session.createQuery(hql);
            query.setParameter("username", username);
            Long count = (Long) query.uniqueResult();
            return count != null && count > 0;
        } finally {
            session.close();
        }
    }
}