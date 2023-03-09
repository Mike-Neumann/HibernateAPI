package me.xra1ny.hibernateapi;

import lombok.Getter;
import me.xra1ny.hibernateapi.annotations.ServiceInfo;
import me.xra1ny.hibernateapi.exceptions.NotAnnotatedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicService {
    @Getter(onMethod = @__(@NotNull))
    private final HibernateConfiguration hibernateConfiguration;

    @Getter(onMethod = @__({ @NotNull, @Unmodifiable}))
    private final List<BasicDao> daos = new ArrayList<>();

    public BasicService(@NotNull HibernateConfiguration hibernateConfiguration) {
        final ServiceInfo info = getClass().getDeclaredAnnotation(ServiceInfo.class);

        if(info == null) {
            throw new NotAnnotatedException(getClass(), ServiceInfo.class);
        }

        this.hibernateConfiguration = hibernateConfiguration;

        for(Class<? extends BasicDao> dao : info.daos()) {
            getDao(dao);
        }
    }

    @NotNull
    public <T> T getDao(@NotNull Class<T> daoClass) {
        T dao = daoClass.cast(this.daos.stream()
                .filter(_dao -> _dao.getClass().equals(daoClass))
                .findFirst()
                .orElse(null));

        if(dao == null) {
            try {
                dao = daoClass.getDeclaredConstructor(BasicService.class).newInstance(this);

                this.daos.add((BasicDao) dao);

                return dao;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }else {
            return dao;
        }
    }
}
