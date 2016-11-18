package org.soneira.score.junit.annotations;

import org.soneira.score.junit.persistence.PersistUnit;
import org.soneira.score.junit.persistence.StaticMap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Persist {

    Class<? extends PersistUnit> value() default StaticMap.class;
}
