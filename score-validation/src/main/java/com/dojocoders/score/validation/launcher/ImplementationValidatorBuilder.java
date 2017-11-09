package com.dojocoders.score.validation.launcher;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dojocoders.score.validation.listener.LoggerListener;
import com.dojocoders.score.validation.listener.ResultListener;
import com.dojocoders.score.validation.persistence.DisabledValidationPublisher;
import com.dojocoders.score.validation.persistence.ValidationPublisher;

public class ImplementationValidatorBuilder<T> {

	private ValidationPublisher validationPublisherImplementation = new DisabledValidationPublisher();
	private int threadPoolSize = 1;
	private T implementation;
	private String team;

	private ImplementationValidatorBuilder() {
	}

	public static <R> ImplementationValidatorBuilder<R> start() {
		return new ImplementationValidatorBuilder<>();
	}

	public ImplementationValidatorBuilder<T> forTeam(String team) {
		this.team = team;
		return this;
	}

	public ImplementationValidatorBuilder<T> withImplementation(T implementation) {
		this.implementation = implementation;
		return this;
	}

	public ImplementationValidatorBuilder<T> withParallelValidation() {
		return withParallelValidation(Runtime.getRuntime().availableProcessors());
	}

	public ImplementationValidatorBuilder<T> withParallelValidation(int nbParallelValidationThreads) {
		threadPoolSize = nbParallelValidationThreads;
		return this;
	}

	public ImplementationValidatorBuilder<T> withValidationPublisher(ValidationPublisher validationPublisher) {
		validationPublisherImplementation = validationPublisher;
		return this;
	}

	public ImplementationValidator<T> build() {
		validateState();
		ExecutorService executor = threadPoolSize > 1 ? Executors.newFixedThreadPool(threadPoolSize) : Executors.newSingleThreadExecutor();
		return new ImplementationValidator<T>(implementation, executor, new ResultListener(validationPublisherImplementation, team),
				new LoggerListener());
	}

	private void validateState() {
		Objects.requireNonNull(implementation, "Implementation must be defined via the withImplementation method");
		Objects.requireNonNull(team, "Team must be defined via the forTeam method");
	}
}
