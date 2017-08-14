package com.dojocoders.score.validation.launcher;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dojocoders.score.validation.listener.LoggerListener;
import com.dojocoders.score.validation.listener.ResultListener;
import com.dojocoders.score.validation.persistence.DisabledValidationPublisher;
import com.dojocoders.score.validation.persistence.ValidationPublisher;

public class ImplementationValidatorBuilder<Implementation> {

	private ValidationPublisher validationPublisherImplementation = new DisabledValidationPublisher();
	private int threadPoolSize = 1;
	private Implementation implementation;
	private String team;

	private ImplementationValidatorBuilder() {
	}

	public static <Implementation> ImplementationValidatorBuilder<Implementation> start(Class<Implementation> implementationClass) {
		return new ImplementationValidatorBuilder<>();
	}

	public ImplementationValidatorBuilder<Implementation> forTeam(String team) {
		this.team = team;
		return this;
	}

	public ImplementationValidatorBuilder<Implementation> withImplementation(Implementation implementation) {
		this.implementation = implementation;
		return this;
	}

	public ImplementationValidatorBuilder<Implementation> withParallelValidation() {
		return withParallelValidation(Runtime.getRuntime().availableProcessors());
	}

	public ImplementationValidatorBuilder<Implementation> withParallelValidation(int nbParallelValidationThreads) {
		threadPoolSize = nbParallelValidationThreads;
		return this;
	}

	public ImplementationValidatorBuilder<Implementation> withValidationPublisher(ValidationPublisher validationPublisher) {
		validationPublisherImplementation = validationPublisher;
		return this;
	}

	public ImplementationValidator<Implementation> build() {
		validateState();
		ExecutorService executor = threadPoolSize > 1 ? Executors.newFixedThreadPool(threadPoolSize) : Executors.newSingleThreadExecutor();
		return new ImplementationValidator<Implementation>(implementation, executor, new ResultListener(validationPublisherImplementation, team), new LoggerListener());
	}

	private void validateState() {
		Objects.requireNonNull(implementation, "Implementation must be defined via the withImplementation method");
		Objects.requireNonNull(team, "Team must be defined via the forTeam method");
	}

}
