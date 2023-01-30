package me.xra1ny.hibernateapi;

import me.xra1ny.hibernateapi.exceptions.NotAnnotatedException;

import javax.persistence.Entity;

public class BasicEntity {
    protected BasicEntity() {
        final Entity entity = getClass().getDeclaredAnnotation(Entity.class);

        if(entity == null) {
            throw new NotAnnotatedException(getClass(), Entity.class);
        }
    }
}
