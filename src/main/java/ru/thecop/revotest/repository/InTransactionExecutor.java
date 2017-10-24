package ru.thecop.revotest.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class InTransactionExecutor {

    @Inject
    private EntityManager em;

    private Session getSession() {
        return em.unwrap(Session.class);
    }

    public <T> T executeInTransaction(TxExecutable<T> executable) {
        T result = null;
        Session session = null;
        Transaction tx = null;
        try {
            session = getSession();
            tx = session.beginTransaction();

            result = executable.execute();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            //TODO use proper logging
            e.printStackTrace();
        }
        return result;
    }
}
