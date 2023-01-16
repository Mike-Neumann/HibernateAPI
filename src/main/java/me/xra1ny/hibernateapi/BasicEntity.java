package me.xra1ny.hibernateapi;

import jakarta.persistence.Entity;
import me.xra1ny.hibernateapi.exceptions.NotAnnotatedException;

public class BasicEntity {
    protected BasicEntity() {
        final Entity entity = getClass().getDeclaredAnnotation(Entity.class);

        if(entity == null) {
            throw new RuntimeException(new NotAnnotatedException(getClass(), Entity.class));
        }
    }
}
