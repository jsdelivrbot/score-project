package com.dojocoders.score.validation.launcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ValidationCasesBuilder<T> {

	private List<ValidationCase<T>> validationCases = new ArrayList<>();

	private ValidationCasesBuilder() {
	}

	public static <R> ValidationCasesBuilder<R> start() {
		return new ValidationCasesBuilder<>();
	}

	public ValidationCasesBuilder<T> withLambdaCase(Consumer<T> lambdaValidationCase) {
		ValidationCase<T> validationCaseToAdd = new ValidationCase<T>(lambdaValidationCase);
		this.validationCases.add(validationCaseToAdd);
		return this;
	}

	public ValidationCasesBuilder<T> withLambdaCases(Consumer<T>... lambdaValidationCases) {
		Arrays.asList(lambdaValidationCases).forEach(this::withLambdaCase);
		return this;
	}

	public ValidationCasesBuilder<T> withLambdaCases(Iterable<Consumer<T>> lambdaValidationCases) {
		lambdaValidationCases.forEach(this::withLambdaCase);
		return this;
	}

	public ValidationCasesBuilder<T> withStaticMethodCase(Method staticMethodValidationCase) {
		ValidationCase<T> validationCaseToAdd = new ValidationCase<T>(staticMethodValidationCase);
		this.validationCases.add(validationCaseToAdd);
		return this;
	}

	public ValidationCasesBuilder<T> withStaticMethodCases(Method... staticMethodValidationCases) {
		Arrays.asList(staticMethodValidationCases).forEach(this::withStaticMethodCase);
		return this;
	}

	public ValidationCasesBuilder<T> withStaticMethodCases(Iterable<Method> staticMethodValidationCases) {
		staticMethodValidationCases.forEach(this::withStaticMethodCase);
		return this;
	}

	public ValidationCasesBuilder<T> withInstanceMethodCase(Object validationInstance, Method instanceMethodValidationCase) {
		ValidationCase<T> validationCaseToAdd = new ValidationCase<>(instanceMethodValidationCase, validationInstance);
		this.validationCases.add(validationCaseToAdd);
		return this;
	}

	public ValidationCasesBuilder<T> withInstanceMethodCases(Object validationInstance, Method... instanceMethodValidationCases) {
		Arrays.asList(instanceMethodValidationCases).forEach(validationCase -> withInstanceMethodCase(validationInstance, validationCase));
		return this;
	}

	public ValidationCasesBuilder<T> withInstanceMethodCases(Object validationInstance, Iterable<Method> instanceMethodValidationCases) {
		instanceMethodValidationCases.forEach(validationCase -> withInstanceMethodCase(validationInstance, validationCase));
		return this;
	}

	public List<ValidationCase<T>> build() {
		return new ArrayList<>(validationCases);
	}
}
