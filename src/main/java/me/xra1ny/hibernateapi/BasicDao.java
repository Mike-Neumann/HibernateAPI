package me.xra1ny.hibernateapi;

import java.util.List;
import java.util.Map;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.hibernateapi.annotations.DaoInfo;
import me.xra1ny.hibernateapi.exceptions.NotAnnotatedException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public <T> T getObject(@NotNull Class<T> type, @NotNull Map<String, Object> columnValues) {
        return getObjects(type, columnValues).stream().findFirst().orElse(null);
    }

    @NotNull
    public <T> List<T> getObjects(@NotNull Class<T> type, @NotNull Map<String, Object> columnValues) {
        try(Session session = hibernateConfiguration.getSessionFactory().openSession()) {
            CriteriaQuery<T> query = session.getCriteriaBuilder().createQuery(type);
            final Root<T> root = query.from(type);
            for(Map.Entry<String, Object> entry : columnValues.entrySet()) {
                query = query.select(root).where(session.getCriteriaBuilder().equal(root.get(entry.getKey()), entry.getValue()));
            }

            return session.createQuery(query).getResultList();
        }
    }

    @Nullable
    public <T> T getObject(@NotNull Class<T> type, @NotNull String column, @NotNull Object value) {
        return getObjects(type, column, value).stream().findFirst().orElse(null);
    }

    @NotNull
    public <T> List<T> getObjects(@NotNull Class<T> type, @NotNull String column, @NotNull Object value) {
        try(Session session = hibernateConfiguration.getSessionFactory().openSession()) {
            CriteriaQuery<T> query = session.getCriteriaBuilder().createQuery(type);
            final Root<T> root = query.from(type);
            query = query.select(root).where(session.getCriteriaBuilder().equal(root.get(column), value));

            return session.createQuery(query).getResultList();
        }
    }

    /**
     * @return A List of all Entries matching the specified Class Type (Table, Entity)
     */
    @NotNull
    public <T> List<T> getAllObjects(@NotNull Class<T> type) {
        try(Session session = hibernateConfiguration.getSessionFactory().openSession()) {
            final CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(type);
            final Root<T> rootEntry = criteriaQuery.from(type);
            final CriteriaQuery<T> all = criteriaQuery.select(rootEntry);

            final TypedQuery<T> allQuery = session.createQuery(all);
            return allQuery.getResultList();
        }
    }

    @NotNull
    public <T> List<T> getAllObjects(@NotNull Class<T> type, @NotNull Object... ids) {
        try(Session session = hibernateConfiguration.getSessionFactory().openSession()) {
            final CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(type);
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
