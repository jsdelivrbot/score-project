package com.dojocoders.score.junit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dojocoders.score.junit.TestConfiguration;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Score {

    int value() default TestConfiguration.DEFAULT_METHOD_POINTS;

    boolean maxTimeOnly() default false;
}
