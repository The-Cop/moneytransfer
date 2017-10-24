package ru.thecop.revotest.repository;

import org.hibernate.*;
import org.hibernate.criterion.Projections;
import ru.thecop.revotest.model.AbstractEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@SuppressWarnings("resource")
abstract class AbstractHibernateGenericDao<E extends AbstractEntity, ID extends Serializable> {

    @Inject
    private EntityManager em;

    private final Class<E> entityClass;

    @SuppressWarnings("unchecked")
    protected AbstractHibernateGenericDao() {
        entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Session getSession() {
        return em.unwrap(Session.class);
    }

    public void create(E entity) {
        em.persist(entity);
    }

    public void update(E entity) {
        em.merge(entity);
    }

    public void delete(E entity) {
        em.remove(entity);
    }

    public boolean deleteById(ID id) {
        int deletedCount = createQuery("DELETE " + entityClass.getSimpleName() + " WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
        return deletedCount == 1;
    }

    public E getById(ID id) {
        return getById(id, false);
    }

    public E getById(ID id, boolean shouldLock) {
        return prepareIdentifierLoadAccess(shouldLock).load(id);
    }

    public E getReferenceById(ID id) {
        return getReferenceById(id, false);
    }

    public E getReferenceById(ID id, boolean shouldLock) {
        return prepareIdentifierLoadAccess(shouldLock).getReference(id);
    }

    private IdentifierLoadAccess<E> prepareIdentifierLoadAccess(boolean shouldLock) {
        IdentifierLoadAccess<E> loadAccess = getSession().byId(entityClass);
        if (shouldLock) {
            loadAccess.with(LockOptions.UPGRADE);
        }
        return loadAccess;
    }

    public E getByNaturalId(Object naturalId) {
        return getByNaturalId(naturalId, false);
    }

    public E getByNaturalId(Object naturalId, boolean shouldLock) {
        return prepareSimpleNaturalIdLoadAccess(shouldLock).load(naturalId);
    }

    public E getReferenceByNaturalId(Object naturalId) {
        return getReferenceByNaturalId(naturalId, false);
    }

    public E getReferenceByNaturalId(Object naturalId, boolean shouldLock) {
        return prepareSimpleNaturalIdLoadAccess(shouldLock).getReference(naturalId);
    }

    private SimpleNaturalIdLoadAccess<E> prepareSimpleNaturalIdLoadAccess(boolean shouldLock) {
        SimpleNaturalIdLoadAccess<E> loadAccess = getSession().bySimpleNaturalId(entityClass);
        if (shouldLock) {
            loadAccess.with(LockOptions.UPGRADE);
        }
        return loadAccess;
    }

    public List<E> findByIds(List<ID> ids) {
        return getSession().byMultipleIds(entityClass).multiLoad(ids);
    }

    @SuppressWarnings("unchecked")
    public List<E> findAll() {
        return createCriteria().list();
    }

    public List<E> find(int offset, int limit) {
        return find(createCriteria(), offset, limit);
    }

    public long count() {
        return count(createCriteria());
    }

    protected long count(Criteria criteria) {
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.uniqueResult()).longValue();
    }

    protected Criteria createCriteria() {
        return getSession().createCriteria(entityClass);
    }

    protected Criteria createCriteria(String alias) {
        return getSession().createCriteria(entityClass, alias);
    }

    protected Query createQuery(String query) {
        return em.createQuery(query);
    }

    protected TypedQuery<E> createTypedQuery(String query) {
        return em.createQuery(query, entityClass);
    }

    protected Query createNativeQuery(String query) {
        return em.createNativeQuery(query);
    }

    protected Query createTypedNativeQuery(String query) {
        return em.createNativeQuery(query, entityClass);
    }

    @SuppressWarnings("unchecked")
    protected List<E> find(Criteria criteria, int offset, int limit) {
        return criteria
                .setFirstResult(offset)
                .setMaxResults(limit)
                .list();
    }

    /**
     * WARNING! Don't use it if your {@link Query} wasn't created using
     * {@link #createTypedQuery(String)} or {@link #createTypedNativeQuery(String)}.
     */
    @SuppressWarnings("unchecked")
    protected List<E> find(Query query, int offset, int limit) {
        return query
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
