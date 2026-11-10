/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.feeStructure;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import util.HibernateUtil;

/**
 *
 * @author shema
 */
public class FeeStructureDao implements GenericDao<feeStructure, Long> {

    @Override
    public void save(feeStructure entity) {
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
    public feeStructure findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return (feeStructure) session.get(feeStructure.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<feeStructure> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM feeStructure");
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(feeStructure entity) {
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
    public void delete(feeStructure entity) {
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

    // 1. Find the fee structure for a specific class and term
    public feeStructure findByClassAndTerm(String classLevel, String term) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM feeStructure f WHERE f.classLevel = :classLevel AND f.term = :term";
            Query query = session.createQuery(hql);
            query.setParameter("classLevel", classLevel);
            query.setParameter("term", term);
            
            // Returns a single feeStructure object (assuming one fee per class per term)
            return (feeStructure) query.uniqueResult(); 
        } finally {
            session.close();
        }
    }
}