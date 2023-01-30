package me.xra1ny.hibernateapi;

import lombok.Getter;
import me.xra1ny.hibernateapi.annotations.HibernateConfigurationInfo;
import me.xra1ny.hibernateapi.exceptions.NotAnnotatedException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HibernateConfiguration {
    @Getter(onMethod = @__(@NotNull))
    private final Configuration configuration;

    @Getter(onMethod = @__(@NotNull))
    private SessionFactory sessionFactory;

    @Getter(onMethod = @__(@NotNull))
    private final List<BasicService> registeredServices = new ArrayList<>();

    @Getter(onMethod = @__(@NotNull))
    private final List<BasicDao> registeredDaos = new ArrayList<>();

    public HibernateConfiguration() {
        final HibernateConfigurationInfo info = getClass().getDeclaredAnnotation(HibernateConfigurationInfo.class);

        if(info == null) {
            throw new NotAnnotatedException(HibernateConfiguration.class, HibernateConfigurationInfo.class);
        }

        this.configuration = new Configuration().configure(info.hibernateCfgXmlUrl());
        this.sessionFactory = configuration.buildSessionFactory();
    }

    public HibernateConfiguration(@NotNull String hibernateCfgXmlUrl) {
        this.configuration = new Configuration().configure(hibernateCfgXmlUrl);
        this.sessionFactory = configuration.buildSessionFactory();
    }

    /**
     * Revalidates the Session Factory of this Hibernate Configuration by reregistering all Entities on all Daos and rebuilding the Session Factory
     */
    public void revalidateSessionFactory() {
        for(BasicDao dao : registeredDaos) {
            for(Class<? extends BasicEntity> entityClass : dao.getEntities()) {
                configuration.addAnnotatedClass(entityClass);
            }
        }

        sessionFactory = configuration.buildSessionFactory();
    }

    @NotNull
    public <T> T getService(@NotNull Class<T> serviceClass) {
        T service = serviceClass.cast(registeredServices.stream()
                .filter(_service -> _service.getClass().equals(serviceClass))
                .findFirst()
                .orElse(null));

        if(service == null) {
            try {
                service = serviceClass.getDeclaredConstructor(HibernateConfiguration.class).newInstance(this);

                registeredServices.add((BasicService) service);

                return service;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            return service;
        }
    }

    @Nullable
    public <T> T getDao(@NotNull Class<T> daoClass) {
        T dao = daoClass.cast(registeredDaos.stream()
                .filter(_dao -> _dao.getClass().equals(daoClass))
                .findFirst()
                .orElse(null));

        if(dao == null) {
            try {
                dao = daoClass.getDeclaredConstructor(HibernateConfiguration.class).newInstance(this);
                registeredDaos.add((BasicDao) dao);
                revalidateSessionFactory();

                return dao;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            return dao;
        }
    }
}
