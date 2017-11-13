package com.dojocoders.score.validation.launcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import com.dojocoders.score.validation.listener.LoggerListener;
import com.dojocoders.score.validation.listener.ResultListener;
import com.dojocoders.score.validation.listener.ValidationListener;
import com.dojocoders.score.validation.persistence.DisabledValidationPublisher;
import com.dojocoders.score.validation.persistence.ValidationPublisher;

public class ImplementationValidatorBuilder {

	private static final long NO_MAXIMUM_VALIDATION_TIME = Long.MAX_VALUE;
	private static final int ONE_THREAD_POOL_SIZE = 1;

	private Object implementationToValidate;

	private List<ValidationCase> validationCases = new ArrayList<>();
	private ValidationPublisher validationPublisherImplementation = new DisabledValidationPublisher();

	private int threadPoolSize = ONE_THREAD_POOL_SIZE;
	private long maximumValidationTimeInSeconds = NO_MAXIMUM_VALIDATION_TIME;

	/**
	 * @deprecated TODO put team as program parameter (mandatory or optional)
	 */
	@Deprecated
	private String team;

	private ImplementationValidatorBuilder(Object implementationToValidate) {
		this.implementationToValidate = implementationToValidate;
	}

	public static ImplementationValidatorBuilder start(Object implementationToValidate) {
		return new ImplementationValidatorBuilder(implementationToValidate);
	}

	public ImplementationValidator build() {
		validateState();

		ExecutorService executor = threadPoolSize > ONE_THREAD_POOL_SIZE ? Executors.newFixedThreadPool(threadPoolSize)
				: Executors.newSingleThreadExecutor();

		List<ValidationListener> listeners = new ArrayList<>();
		listeners.add(new ResultListener(validationPublisherImplementation, team));
		listeners.add(new LoggerListener());

		return new ImplementationValidator(executor, maximumValidationTimeInSeconds, validationCases, listeners);
	}

	public ImplementationValidatorBuilder forTeam(String team) {
		this.team = team;
		return this;
	}

	public ImplementationValidatorBuilder withMaximumValidationTimeInSeconds(long maximumValidationTimeInSeconds) {
		this.maximumValidationTimeInSeconds = maximumValidationTimeInSeconds;
		return this;
	}

	public ImplementationValidatorBuilder withParallelValidation() {
		return withParallelValidation(Runtime.getRuntime().availableProcessors());
	}

	public ImplementationValidatorBuilder withParallelValidation(int nbParallelValidationThreads) {
		threadPoolSize = nbParallelValidationThreads;
		return this;
	}

	public ImplementationValidatorBuilder withValidationPublisher(ValidationPublisher validationPublisher) {
		validationPublisherImplementation = validationPublisher;
		return this;
	}

	public ImplementationValidatorBuilder withValidationCasesAnnotedBy(Class<? extends Annotation> annotation) {
		return this.withValidationCasesAnnotedBy(implementationToValidate.getClass().getPackage().getName(), annotation);
	}

	public ImplementationValidatorBuilder withValidationCasesAnnotedBy(String basePackage, Class<? extends Annotation> annotation) {
		Reflections reflections = new Reflections(basePackage, new MethodAnnotationsScanner());
		Set<Method> reflectiveValidationCases = reflections.getMethodsAnnotatedWith(annotation);

		for (Method validationCase : reflectiveValidationCases) {
			this.validationCases.add(new ValidationCase(validationCase, implementationToValidate));
		}

		return this;
	}

	/**
	 * @deprecated TODO put team as program parameter (mandatory or optional)
	 */
	@Deprecated
	private void validateState() {
		Objects.requireNonNull(team, "Team must be defined via the forTeam method");
	}

}
