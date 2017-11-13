package com.dojocoders.score.validation.launcher;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.util.ClassUtils;

import com.google.common.base.Throwables;

// TODO Add Smart failure (validation) exception management
// TODO Add a javadoc
public class ValidationCase {

	private static final Object STATIC_INSTANCE = null;

	private Method caseDescription;
	private Object implementationToValidate;

	public ValidationCase(Method caseDescription, Object implementationToValidate) {
		this.caseDescription = caseDescription;
		this.implementationToValidate = implementationToValidate;
		assertStaticValidationCase();
	}

	public Method getCaseDescription() {
		return caseDescription;
	}

	public Object callValidationCase() throws Exception {
		try {
			return caseDescription.invoke(STATIC_INSTANCE, implementationToValidate);
		} catch (InvocationTargetException e) {
			Throwables.throwIfUnchecked(e.getTargetException());
			Throwables.throwIfInstanceOf(e.getTargetException(), Exception.class);
			// Sould never append, as a Throwable in an Error or an Exception
			throw e;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void assertStaticValidationCase() {
		checkMethod(Modifier.isStatic(caseDescription.getModifiers()), "Validation method must be static");
		checkMethod(Modifier.isPublic(caseDescription.getModifiers()), "Validation method must be public");
		checkMethod(caseDescription.getParameterCount() == 1, "Validation method must take only one parameter");
		checkMethod(ClassUtils.isAssignable(caseDescription.getParameterTypes()[0], implementationToValidate.getClass()),
				"Validation method does not take an instance of " + implementationToValidate.getClass().getSimpleName() + " as parameter");
	}

	private void checkMethod(boolean assertion, String faillingMessage) {
		checkState(assertion, "%s.%s : %s", caseDescription.getDeclaringClass().getName(), caseDescription.getName(), faillingMessage);
	}
}
