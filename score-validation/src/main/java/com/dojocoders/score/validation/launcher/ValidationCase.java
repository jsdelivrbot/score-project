package com.dojocoders.score.validation.launcher;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.base.Throwables;

public class ValidationCase<Implementation> {

	private Method caseDescription;
	private Consumer<Implementation> caseAccessor;

	public ValidationCase(Method caseDescription) {
		this.caseDescription = caseDescription;
		this.caseAccessor = (implementation -> callCase(implementation, null));
	}

	public ValidationCase(Method caseDescription, Object caseInstance) {
		this.caseDescription = caseDescription;
		this.caseAccessor = (implementation -> callCase(implementation, caseInstance));
	}

	public ValidationCase(Consumer<Implementation> caseAccessor) {
		this.caseAccessor = caseAccessor;
		this.caseDescription = createCaseDescription();
	}

	public Method getCaseDescription() {
		return caseDescription;
	}

	public Consumer<Implementation> getCaseAccessor() {
		return caseAccessor;
	}

	private void callCase(Implementation implementation, Object instance) {
		try {
			caseDescription.invoke(instance, implementation);
		} catch (InvocationTargetException e) {
			Throwables.throwIfUnchecked(e.getTargetException());
			throw new RuntimeException(e.getTargetException());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private Method createCaseDescription() {
		StackTraceElement currentMethod = null;
		try {
			caseDescription.getClass();
		} catch (NullPointerException e) {
			currentMethod = e.getStackTrace()[0];
		}
		checkState(currentMethod != null, "NullPointerException must be thrown");

		StackTraceElement calledMethod = null;
		try {
			caseAccessor.accept(null);
		} catch (NullPointerException e) {
			calledMethod = foundCalledMethod(currentMethod, e.getStackTrace());
		}
		checkState(calledMethod != null, "NullPointerException must be thrown");

		try {
			Class<?> calledClass = Class.forName(calledMethod.getClassName());
			return findMethod(calledMethod.getMethodName(), Arrays.asList(calledClass.getDeclaredMethods()));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private Method findMethod(String methodName, Collection<Method> methods) {
		List<Method> matchedMethods = methods.stream() //
				.filter(m -> m.getName().equals(methodName)) //
				.filter(m -> m.getReturnType().equals(Void.TYPE)) //
				.filter(m -> m.getParameterTypes().length == 1) //
				.collect(Collectors.toList());
		checkState(matchedMethods.size() == 1, "Incorrect number of method " + methodName);
		return matchedMethods.get(0);
	}

	private StackTraceElement foundCalledMethod(StackTraceElement currentMethod, StackTraceElement[] callStackTrace) {
		Comparator<StackTraceElement> comparator = Comparator.comparing(StackTraceElement::getMethodName).thenComparing(StackTraceElement::getClassName);
		StackTraceElement previousMethod = null;
		for (StackTraceElement stackTraceElement : callStackTrace) {
			if (comparator.compare(currentMethod, stackTraceElement) == 0) {
				checkState(previousMethod != null, "Incorrect stacktrace " + callStackTrace);
				return previousMethod;
			}
			previousMethod = stackTraceElement;
		}
		throw new IllegalStateException("currentMethod " + currentMethod + " not found in callStackTrace " + callStackTrace);
	}
}
