package com.dojocoders.score.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dojocoders.score.validation.persistence.ScorePublisher;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Persist {

	Class<? extends ScorePublisher> value();

}
