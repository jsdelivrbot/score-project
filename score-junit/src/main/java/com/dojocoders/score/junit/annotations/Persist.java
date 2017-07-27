package com.dojocoders.score.junit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dojocoders.score.junit.persistence.ScorePersistenceUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Persist {

	Class<? extends ScorePersistenceUnit> value();

}
