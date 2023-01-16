package me.xra1ny.hibernateapi;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class BasicService {
    @Getter(onMethod = @__(@NotNull))
    private final HibernateConfiguration hibernateConfiguration;

    protected BasicService(@NotNull HibernateConfiguration hibernateConfiguration) {
        this.hibernateConfiguration = hibernateConfiguration;
    }
}
