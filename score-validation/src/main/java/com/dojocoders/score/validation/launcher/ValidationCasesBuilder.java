package com.dojocoders.score.validation.launcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ValidationCasesBuilder<Implementation> {

	private List<ValidationCase<Implementation>> validationCases;

	private ValidationCasesBuilder() {
	}

	public static <Implementation> ValidationCasesBuilder<Implementation> start(Class<Implementation> implementationClass) {
		return new ValidationCasesBuilder<>();
	}

	public ValidationCasesBuilder<Implementation> withLambdaCase(Consumer<Implementation> lambdaValidationCase) {
		ValidationCase<Implementation> validationCaseToAdd = new ValidationCase<Implementation>(lambdaValidationCase);
		this.validationCases.add(validationCaseToAdd);
		return this;
	}

	public ValidationCasesBuilder<Implementation> withLambdaCases(Consumer<Implementation>... lambdaValidationCases) {
		Arrays.asList(lambdaValidationCases).forEach(this::withLambdaCase);
		return this;
	}

	public ValidationCasesBuilder<Implementation> withStaticMethodCase(Method staticMethodValidationCase) {
		ValidationCase<Implementation> validationCaseToAdd = new ValidationCase<Implementation>(staticMethodValidationCase);
		this.validationCases.add(validationCaseToAdd);
		return this;
	}

	public ValidationCasesBuilder<Implementation> withStaticMethodCases(Method... staticMethodValidationCases) {
		Arrays.asList(staticMethodValidationCases).forEach(this::withStaticMethodCase);
		return this;
	}

	public ValidationCasesBuilder<Implementation> withInstanceMethodCase(Object validationInstance, Method instanceMethodValidationCase) {
		ValidationCase<Implementation> validationCaseToAdd = new ValidationCase<Implementation>(instanceMethodValidationCase, validationInstance);
		this.validationCases.add(validationCaseToAdd);
		return this;
	}

	public ValidationCasesBuilder<Implementation> withInstanceMethodCases(Object validationInstance, Method... instanceMethodValidationCases) {
		Arrays.asList(instanceMethodValidationCases).forEach(validationCase -> withInstanceMethodCase(validationInstance, validationCase));
		return this;
	}

	public List<ValidationCase<Implementation>> build() {
		return new ArrayList<>(validationCases);
	}
}
