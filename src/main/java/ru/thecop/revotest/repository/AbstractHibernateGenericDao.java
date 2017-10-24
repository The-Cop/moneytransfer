package ru.thecop.revotest.repository;

import com.google.inject.Provider;
import org.hibernate.*;
import ru.thecop.revotest.model.AbstractEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@SuppressWarnings("resource")
abstract class AbstractHibernateGenericDao<E extends AbstractEntity, ID extends Serializable> {

    @Inject
    private Provider<EntityManager> em;

    private final Class<E> entityClass;

    @SuppressWarnings("unchecked")
    protected AbstractHibernateGenericDao() {
        entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Session getSession() {
        return em.get().unwrap(Session.class);
    }

    public void create(E entity) {
        em.get().persist(entity);
    }

    public void update(E entity) {
        em.get().merge(entity);
    }

    public void delete(E entity) {
        em.get().remove(entity);
    }

    public E getById(ID id) {
        return getById(id, false);
    }

    public E getById(ID id, boolean shouldLock) {
        return prepareIdentifierLoadAccess(shouldLock).load(id);
    }

    private IdentifierLoadAccess<E> prepareIdentifierLoadAccess(boolean shouldLock) {
        IdentifierLoadAccess<E> loadAccess = getSession().byId(entityClass);
        if (shouldLock) {
            loadAccess.with(LockOptions.UPGRADE);
        }
        return loadAccess;
    }


    @SuppressWarnings("unchecked")
    public List<E> findAll() {
        return createCriteria().list();
    }

    protected Criteria createCriteria() {
        return getSession().createCriteria(entityClass);
    }

}
