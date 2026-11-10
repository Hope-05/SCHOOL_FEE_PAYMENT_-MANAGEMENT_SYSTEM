/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.payment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query; 
import util.HibernateUtil;

/**
 *
 * @author shema
 */
public class PaymentDao implements GenericDao<payment, Long> {

    @Override
    public void save(payment entity) {
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
    public payment findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return (payment) session.get(payment.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<payment> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM payment");
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(payment entity) {
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
    public void delete(payment entity) {
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

    // 1. Check if reference exists (Rule: Duplicate Reference)
    public boolean referenceExists(String reference) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "SELECT COUNT(p) FROM payment p WHERE p.reference = :ref";
            Query query = session.createQuery(hql);
            query.setParameter("ref", reference);
            
            // Cast the result to Long
            Long count = (Long) query.uniqueResult();
            return count != null && count > 0;
        } finally {
            session.close();
        }
    }

    // 2. Find payments by invoice ID
    @SuppressWarnings("unchecked")
    public List<payment> findByInvoice(Long invoiceId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM payment p WHERE p.invoiceId = :invoiceId";
            Query query = session.createQuery(hql);
            query.setParameter("invoiceId", invoiceId);
            
            // Cast the result to a List of payments
            return (List<payment>) query.list();
        } finally {
            session.close();
        }
    }
}