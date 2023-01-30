package me.xra1ny.hibernateapi.exceptions;

import org.jetbrains.annotations.NotNull;

public class AlreadyRegisteredException extends RuntimeException {
    public AlreadyRegisteredException(@NotNull Class<?> clazz) {
        super(clazz.getName() + " is already registered on this Hibernate Configuration!");
    }
}
