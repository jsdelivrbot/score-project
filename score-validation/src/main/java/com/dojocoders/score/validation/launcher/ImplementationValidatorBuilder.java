package com.dojocoders.score.validation.launcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import com.dojocoders.score.validation.listener.LoggerListener;
import com.dojocoders.score.validation.listener.ValidationListener;

public class ImplementationValidatorBuilder {

	private static final long NO_MAXIMUM_VALIDATION_TIME = Long.MAX_VALUE;
	private static final int ONE_THREAD_POOL_SIZE = 1;

	private Object implementationToValidate;

	private List<ValidationCase> validationCases = new ArrayList<>();
	private List<ValidationListener> validationListeners = new ArrayList<>();
	private boolean withLoggingListener = true;

	private int threadPoolSize = ONE_THREAD_POOL_SIZE;
	private long maximumValidationTimeInSeconds = NO_MAXIMUM_VALIDATION_TIME;

	private ImplementationValidatorBuilder(Object implementationToValidate) {
		this.implementationToValidate = implementationToValidate;
	}

	public static ImplementationValidatorBuilder start(Object implementationToValidate) {
		return new ImplementationValidatorBuilder(implementationToValidate);
	}

	public ImplementationValidator build() {
		ExecutorService executor = threadPoolSize > ONE_THREAD_POOL_SIZE ? Executors.newFixedThreadPool(threadPoolSize)
				: Executors.newSingleThreadExecutor();

		if (withLoggingListener) {
			validationListeners.add(new LoggerListener());
		}

		return new ImplementationValidator(executor, maximumValidationTimeInSeconds, validationCases, validationListeners);
	}

	public ImplementationValidatorBuilder withoutLogging() {
		this.withLoggingListener = false;
		return this;
	}

	public ImplementationValidatorBuilder withListeners(ValidationListener... listener) {
		this.validationListeners.addAll(Arrays.asList(listener));
		return this;
	}

	public ImplementationValidatorBuilder withMaximumValidationTimeInSeconds(long maximumValidationTimeInSeconds) {
		this.maximumValidationTimeInSeconds = maximumValidationTimeInSeconds;
		return this;
	}

	public ImplementationValidatorBuilder withParallelValidation() {
		threadPoolSize = Runtime.getRuntime().availableProcessors();
		return this;
	}

	public ImplementationValidatorBuilder withParallelValidation(int nbParallelValidationThreads) {
		threadPoolSize = nbParallelValidationThreads;
		return this;
	}

	public ImplementationValidatorBuilder withValidationCasesAnnotedBy(Class<? extends Annotation> annotation) {
		String basePackage = implementationToValidate.getClass().getPackage().getName();
		addValidationCasesAnnotedBy(basePackage, annotation);
		return this;
	}

	public ImplementationValidatorBuilder withValidationCasesAnnotedBy(String basePackage, Class<? extends Annotation> annotation) {
		addValidationCasesAnnotedBy(basePackage, annotation);
		return this;
	}

	private void addValidationCasesAnnotedBy(String basePackage, Class<? extends Annotation> annotation) {
		Reflections reflections = new Reflections(basePackage, new MethodAnnotationsScanner());
		Set<Method> reflectiveValidationCases = reflections.getMethodsAnnotatedWith(annotation);

		for (Method validationCase : reflectiveValidationCases) {
			this.validationCases.add(new ValidationCase(validationCase, implementationToValidate));
		}
	}

}
