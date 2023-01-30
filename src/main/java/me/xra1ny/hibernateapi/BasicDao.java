package me.xra1ny.hibernateapi;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.hibernateapi.annotations.DaoInfo;
import me.xra1ny.hibernateapi.exceptions.NotAnnotatedException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class BasicDao {
    @Getter(onMethod = @__(@NotNull))
    private final HibernateConfiguration hibernateConfiguration;

    @Getter(onMethod = @__(@NotNull))
    private final Class<? extends BasicEntity>[] entities;

    protected BasicDao(@NotNull HibernateConfiguration hibernateConfiguration) {
        final DaoInfo info = getClass().getDeclaredAnnotation(DaoInfo.class);

        if(info == null) {
            throw new NotAnnotatedException(getClass(), DaoInfo.class);
        }

        this.hibernateConfiguration = hibernateConfiguration;
        this.entities = info.entities();
    }

    /**
     * Persists the specified Object in the Database associated with this Daos Hibernate Configuration
     */
    public <T> T saveObject(@NotNull T object) {
        try(Session session = hibernateConfiguration.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            final T t = (T) session.merge(object);

            transaction.commit();

            return t;
        }
    }

    @NotNull
    public <T> T getObject(@NotNull Class<T> type, @NotNull Object id) {
        try(Session session = hibernateConfiguration.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            final T t = session.find(type, id);

            transaction.commit();

            return t;
        }
    }

    /**
     * @return A List of all Entries matching the specified Class Type (Table, Entity)
     */
    @NotNull
    public <T> List<T> getAllObjects(@NotNull Class<T> type) {
        try(Session session = hibernateConfiguration.getSessionFactory().openSession()) {
            final CriteriaQuery<T> criteriaQuery = (CriteriaQuery<T>) session.getCriteriaBuilder().createQuery();
            final Root<T> rootEntry = criteriaQuery.from(type);
            final CriteriaQuery<T> all = criteriaQuery.select(rootEntry);

            final TypedQuery<T> allQuery = session.createQuery(all);
            return allQuery.getResultList();
        }
    }

    @NotNull
    public <T> List<T> getAllObjects(@NotNull Class<T> type, @NotNull Object... ids) {
        try(Session session = hibernateConfiguration.getSessionFactory().openSession()) {
            final CriteriaQuery<T> criteriaQuery = (CriteriaQuery<T>) session.getCriteriaBuilder().createQuery();
            final Root<T> rootEntry = criteriaQuery.from(type);
            final CriteriaQuery<T> all = criteriaQuery.select(rootEntry).where(rootEntry.in(ids));

            final TypedQuery<T> allQuery = session.createQuery(all);
            return allQuery.getResultList();
        }
    }

    public void removeObject(@NotNull Object object) {
        try(Session session = hibernateConfiguration.getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            session.delete(object);

            transaction.commit();
        }
    }
}
