package com.dojocoders.score.junit.annotations;

import com.dojocoders.score.junit.persistence.TestPersistUnit;
import com.dojocoders.score.junit.persistence.TestStaticMap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Persist {

    Class<? extends TestPersistUnit> value() default TestStaticMap.class;
}
