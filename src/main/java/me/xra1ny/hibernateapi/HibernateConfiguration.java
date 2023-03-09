package me.xra1ny.hibernateapi;

import lombok.Getter;
import me.xra1ny.hibernateapi.annotations.HibernateConfigurationInfo;
import me.xra1ny.hibernateapi.exceptions.NotAnnotatedException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public class HibernateConfiguration {
    @Getter(onMethod = @__(@NotNull))
    private final Configuration configuration;

    @Getter(onMethod = @__(@NotNull))
    private SessionFactory sessionFactory;

    @Getter(onMethod = @__({ @NotNull, @Unmodifiable}))
    private final List<BasicService> registeredServices = new ArrayList<>();

    public HibernateConfiguration() {
        final HibernateConfigurationInfo info = getClass().getDeclaredAnnotation(HibernateConfigurationInfo.class);

        if(info == null) {
            throw new NotAnnotatedException(HibernateConfiguration.class, HibernateConfigurationInfo.class);
        }

        this.configuration = new Configuration().configure(info.hibernateCfgXmlUrl());

        buildSessionFactory(info.services());
    }

    @SafeVarargs
    public HibernateConfiguration(@NotNull String hibernateCfgXmlUrl, @NotNull Class<? extends BasicService>... services) {
        this.configuration = new Configuration().configure(hibernateCfgXmlUrl);

        buildSessionFactory(services);
    }

    /**
     * Builds the Session Factory of this HibernateConfiguration with the specified services
     */
    @SafeVarargs
    private void buildSessionFactory(@NotNull Class<? extends BasicService>... services) {
        for(Class<? extends BasicService> serviceClass : services) {
            final BasicService service = getService(serviceClass);

            for(BasicDao dao : service.getDaos()) {
                for(Class<? extends BasicEntity> entity : dao.getEntities()) {
                    this.configuration.addAnnotatedClass(entity);
                }
            }
        }

        this.sessionFactory = this.configuration.buildSessionFactory();
    }

    /**
     * Retrieves the Service Instance specified by this Service Class. Registering it in the default HibernateConfiguration after Instance Creation
     */
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

                buildSessionFactory((Class<? extends BasicService>) serviceClass);

                return service;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }else {
            return service;
        }
    }
}
