package me.xra1ny.hibernateapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.hibernateapi.annotations.DaoInfo;
import me.xra1ny.hibernateapi.exceptions.NotAnnotatedException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public abstract class BasicDao {
    @Getter(onMethod = @__(@NotNull))
    private final BasicService service;

    @Getter(onMethod = @__(@NotNull))
    private final Class<? extends BasicEntity>[] entities;

    public BasicDao(@NotNull BasicService service) {
        final DaoInfo info = getClass().getDeclaredAnnotation(DaoInfo.class);

        if(info == null) {
            throw new NotAnnotatedException(getClass(), DaoInfo.class);
        }

        this.service = service;
        this.entities = info.entities();
    }

    /**
     * Persists the specified Object in the Database associated with this Daos Hibernate Configuration
     */
    public <T> T saveObject(@NotNull T object) {
        try(Session session = this.service.getHibernateConfiguration().getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            final T t = (T) session.merge(object);

            transaction.commit();

            return t;
        }
    }

    @NotNull
    public <T> T getObject(@NotNull Class<T> type, @NotNull Object id) {
        try(Session session = this.service.getHibernateConfiguration().getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            final T t = session.find(type, id);

            transaction.commit();

            return t;
        }
    }

    @Nullable
    public <T> T getObject(@NotNull Class<T> type, @NotNull Map<String, ?> columnValues) {
        return getObjects(type, columnValues).stream().findFirst().orElse(null);
    }

    @NotNull
    public <T> List<T> getObjects(@NotNull Class<T> type, @NotNull Map<String, ?> columnValues) {
        try(Session session = this.service.getHibernateConfiguration().getSessionFactory().openSession()) {
            final CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(type);
            final Root<T> root = criteriaQuery.from(type);
            criteriaQuery.select(root);

            final List<T> objects = new ArrayList<>();

            for(Map.Entry<String, ?> entry : columnValues.entrySet()) {
                final ParameterExpression<T> parameterExpression = (ParameterExpression<T>) session.getCriteriaBuilder().parameter(entry.getValue().getClass());
                criteriaQuery.where(session.getCriteriaBuilder().equal(root.get(entry.getKey()), parameterExpression));

                final TypedQuery<T> query = session.createQuery(criteriaQuery);
                query.setParameter(parameterExpression, (T) entry.getValue());

                objects.addAll(query.getResultList());
            }

            return objects;
        }
    }

    @Nullable
    public <T> T getObject(@NotNull Class<T> type, @NotNull String column, @NotNull Object value) {
        return getObjects(type, column, value).stream().findFirst().orElse(null);
    }

    @NotNull
    public <T> List<T> getObjects(@NotNull Class<T> type, @NotNull String column, @NotNull Object value) {
        return getObjects(type, Map.of(column, value));
    }

    /**
     * @return A List of all Entries matching the specified Class Type (Table, Entity)
     */
    @NotNull
    public <T> List<T> getAllObjects(@NotNull Class<T> type) {
        try(Session session = this.service.getHibernateConfiguration().getSessionFactory().openSession()) {
            final CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(type);
            final Root<T> rootEntry = criteriaQuery.from(type);
            final CriteriaQuery<T> all = criteriaQuery.select(rootEntry);

            final TypedQuery<T> allQuery = session.createQuery(all);
            return allQuery.getResultList();
        }
    }

    @NotNull
    public <T> List<T> getAllObjects(@NotNull Class<T> type, @NotNull Object... ids) {
        try(Session session = this.service.getHibernateConfiguration().getSessionFactory().openSession()) {
            final CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(type);
            final Root<T> rootEntry = criteriaQuery.from(type);
            final CriteriaQuery<T> all = criteriaQuery.select(rootEntry).where(rootEntry.in(ids));

            final TypedQuery<T> allQuery = session.createQuery(all);
            return allQuery.getResultList();
        }
    }

    public void removeObject(@NotNull Object object) {
        try(Session session = this.service.getHibernateConfiguration().getSessionFactory().openSession()) {
            final Transaction transaction = session.beginTransaction();

            session.delete(object);

            transaction.commit();
        }
    }
}
