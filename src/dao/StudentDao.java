/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.student;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import util.HibernateUtil;

/**
 *
 * @author shema
 */
public class StudentDao implements GenericDao<student, Long> {

    @Override
    public void save(student entity) {
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
    public student findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return (student) session.get(student.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<student> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM student");
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(student entity) {
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
    public void delete(student entity) {
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

    // 1. Find all students in a specific class level (e.g., "Year 1")
    @SuppressWarnings("unchecked")
    public List<student> findByClassLevel(String classLevel) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM student s WHERE s.classLevel = :classLevel";
            Query query = session.createQuery(hql);
            query.setParameter("classLevel", classLevel);
            return (List<student>) query.list();
        } finally {
            session.close();
        }
    }
    
    // 2. Find all active students
    @SuppressWarnings("unchecked")
    public List<student> findActiveStudents() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM student s WHERE s.status = :status";
            Query query = session.createQuery(hql);
            query.setParameter("status", "ACTIVE");
            return (List<student>) query.list();
        } finally {
            session.close();
        }
    }
}