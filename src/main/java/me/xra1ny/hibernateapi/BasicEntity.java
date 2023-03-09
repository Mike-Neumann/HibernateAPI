package me.xra1ny.hibernateapi;

import me.xra1ny.hibernateapi.exceptions.NotAnnotatedException;

import jakarta.persistence.Entity;

public abstract class BasicEntity {
    public BasicEntity() {
        final Entity entity = getClass().getDeclaredAnnotation(Entity.class);

        if(entity == null) {
            throw new NotAnnotatedException(getClass(), Entity.class);
        }
    }
}
