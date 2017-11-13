package com.dojocoders.score.validation.launcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ValidationCaseTest {

	private static final int EXPECTED_VALUE = 11;

	@Test
	public void test_with_simpleCase_method() throws Throwable {
		// Setup
		ValidationCase validationCase = createValidationCase("simpleCase");

		// Test
		Object result = validationCase.callValidationCase();

		// Assert
		assertThat(result).isEqualTo(EXPECTED_VALUE);
	}

	public static Integer simpleCase(AtomicInteger value) {
		return value.get();
	}

	@Test
	public void test_with_boxedCase_method() throws Throwable {
		// Setup
		ValidationCase validationCase = createValidationCase("boxedCase");

		// Test
		Object result = validationCase.callValidationCase();

		// Assert
		assertThat(result).isEqualTo(EXPECTED_VALUE);
	}

	public static int boxedCase(AtomicInteger value) {
		return value.get();
	}

	@Test(expected = IOException.class)
	public void test_with_exception_method() throws Throwable {
		// Setup
		ValidationCase validationCase = createValidationCase("exceptionCase");

		// Test
		validationCase.callValidationCase();
	}

	public static Integer exceptionCase(AtomicInteger value) throws IOException {
		throw new IOException("test for " + value);
	}

	@Test(expected = NoSuchMethodException.class)
	public void test_with_nonExistantCase_method() throws NoSuchMethodException {
		createValidationCase("nonExistantCase");
	}

	@Test(expected = RuntimeException.class)
	public void test_with_nonStaticCase_method() throws NoSuchMethodException {
		createValidationCase("nonStaticCase");
	}

	public Integer nonStaticCase(AtomicInteger value) {
		return simpleCase(value);
	}

	@Test(expected = RuntimeException.class)
	public void test_with_nonPublicCase_method() throws NoSuchMethodException {
		createValidationCase("nonPublicCase");
	}

	protected static Integer nonPublicCase(AtomicInteger value) {
		return simpleCase(value);
	}

	public static Long wrongReturnTypeCase(AtomicInteger value) {
		return simpleCase(value).longValue();
	}

	@Test(expected = NoSuchMethodException.class)
	public void test_with_wrongParameterTypeCase_method() throws NoSuchMethodException {
		createValidationCase("wrongParameterTypeCase");
	}

	public static Integer wrongParameterTypeCase(Integer value) {
		return value;
	}

	@Test(expected = NoSuchMethodException.class)
	public void test_with_noParameterCase_method() throws NoSuchMethodException {
		createValidationCase("noParameterCase");
	}

	public static Integer noParameterCase() {
		return 0;
	}

	@Test(expected = NoSuchMethodException.class)
	public void test_with_tooManyParametersCase_method() throws NoSuchMethodException {
		createValidationCase("tooManyParametersCase");
	}

	public static Integer tooManyParametersCase(AtomicInteger value, Integer anotherParameter) {
		return simpleCase(value) + anotherParameter;
	}

	private ValidationCase createValidationCase(String methodName) throws NoSuchMethodException {
		AtomicInteger i = new AtomicInteger(EXPECTED_VALUE);
		Method method = this.getClass().getDeclaredMethod(methodName, AtomicInteger.class);
		return new ValidationCase(method, i);
	}
}
