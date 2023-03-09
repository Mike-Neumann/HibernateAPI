package me.xra1ny.hibernateapi.annotations;

import me.xra1ny.hibernateapi.BasicService;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HibernateConfigurationInfo {
    @NotNull
    String hibernateCfgXmlUrl();

    @NotNull
    Class<? extends BasicService>[] services();
}
