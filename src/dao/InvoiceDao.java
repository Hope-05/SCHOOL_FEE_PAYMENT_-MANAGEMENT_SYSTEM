/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.invoice;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import util.HibernateUtil;

/**
 *
 * @author shema
 */
public class InvoiceDao implements GenericDao<invoice, Long> {

    @Override
    public void save(invoice entity) {
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
    public invoice findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return (invoice) session.get(invoice.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<invoice> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM invoice");
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(invoice entity) {
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
    public void delete(invoice entity) {
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

    // 1. Find all invoices for a specific student
    @SuppressWarnings("unchecked")
    public List<invoice> findByStudent(Long studentId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM invoice i WHERE i.studentId = :studentId";
            Query query = session.createQuery(hql);
            query.setParameter("studentId", studentId);
            return (List<invoice>) query.list();
        } finally {
            session.close();
        }
    }

    // 2. Find all unpaid or partially paid invoices (Useful for generating reports)
    @SuppressWarnings("unchecked")
    public List<invoice> findUnpaidInvoices() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM invoice i WHERE i.status != :paidStatus";
            Query query = session.createQuery(hql);
            query.setParameter("paidStatus", "PAID");
            return (List<invoice>) query.list();
        } finally {
            session.close();
        }
    }
}