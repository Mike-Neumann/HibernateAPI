package me.xra1ny.hibernateapi.exceptions;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public class NotAnnotatedException extends RuntimeException {
    public NotAnnotatedException(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation) {
        super("Class " + clazz.getName() + " needs to be annotated with " + annotation.getName() + "!");
    }
}
