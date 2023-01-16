package me.xra1ny.hibernateapi.annotations;

import me.xra1ny.hibernateapi.BasicDao;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceInfo {
    @NotNull
    Class<? extends BasicDao>[] daos();
}
