package com.dojocoders.score.validation.launcher;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.dojocoders.score.validation.listener.ValidationListener;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class ImplementationValidatorTest {

	private static final long MAXIMUM_TEST_TIME_IN_SECONDS = 3;
	private static final Integer TESTED_IMPLEMENTATION = new Integer(new Random().nextInt());
	private static final Integer SUCCESS_RESULT = new Integer(new Random().nextInt());
	private static final RuntimeException ERROR_EXCEPTION = new RuntimeException("runtimeException");
	private static final AssertionError FAILURE_EXCEPTION = new AssertionError("assertionError");

	private ImplementationValidator implementationValidator;

	private List<ValidationCase> validationCases;
	private ValidationCase validationCaseWithSuccess;
	private ValidationCase validationCaseWithFailure;
	private ValidationCase validationCaseWithError;

	private Method successMethod;
	private Method failureMethod;
	private Method errorMethod;

	@Mock
	private ValidationListener firstValidationListener;

	@Mock
	private ValidationListener secondValidationListener;

	@Before
	public void setup() throws NoSuchMethodException {
		successMethod = ImplementationValidatorTest.class.getDeclaredMethod("methodWithSuccess", Integer.class);
		validationCaseWithSuccess = new ValidationCase(successMethod, TESTED_IMPLEMENTATION);

		failureMethod = ImplementationValidatorTest.class.getDeclaredMethod("methodWithFailure", Integer.class);
		validationCaseWithFailure = new ValidationCase(failureMethod, TESTED_IMPLEMENTATION);

		errorMethod = ImplementationValidatorTest.class.getDeclaredMethod("methodWithError", Integer.class);
		validationCaseWithError = new ValidationCase(errorMethod, TESTED_IMPLEMENTATION);

		validationCases = new ArrayList<>();
		List<ValidationListener> listeners = Lists.newArrayList(firstValidationListener, secondValidationListener);
		implementationValidator = new ImplementationValidator(Executors.newSingleThreadExecutor(), MAXIMUM_TEST_TIME_IN_SECONDS, validationCases,
				listeners);
	}

	@Test
	public void test_listener_consumer_calls_in_order() {
		// Setup
		validationCases.add(validationCaseWithSuccess);
		validationCases.add(validationCaseWithFailure);
		validationCases.add(validationCaseWithError);

		// Test
		implementationValidator.validate();

		// Assert
		InOrder inOrder = inOrder(firstValidationListener, secondValidationListener);
		inOrder.verify(firstValidationListener).startValidation();
		inOrder.verify(secondValidationListener).startValidation();

		inOrder.verify(firstValidationListener).startCase(successMethod);
		inOrder.verify(secondValidationListener).startCase(successMethod);
		inOrder.verify(firstValidationListener).caseSuccess(successMethod, SUCCESS_RESULT);
		inOrder.verify(secondValidationListener).caseSuccess(successMethod, SUCCESS_RESULT);
		inOrder.verify(firstValidationListener).caseFinished(successMethod);
		inOrder.verify(secondValidationListener).caseFinished(successMethod);

		inOrder.verify(firstValidationListener).startCase(failureMethod);
		inOrder.verify(secondValidationListener).startCase(failureMethod);
		inOrder.verify(firstValidationListener).caseFailure(failureMethod, FAILURE_EXCEPTION);
		inOrder.verify(secondValidationListener).caseFailure(failureMethod, FAILURE_EXCEPTION);
		inOrder.verify(firstValidationListener).caseFinished(failureMethod);
		inOrder.verify(secondValidationListener).caseFinished(failureMethod);

		inOrder.verify(firstValidationListener).startCase(errorMethod);
		inOrder.verify(secondValidationListener).startCase(errorMethod);
		inOrder.verify(firstValidationListener).caseError(errorMethod, ERROR_EXCEPTION);
		inOrder.verify(secondValidationListener).caseError(errorMethod, ERROR_EXCEPTION);
		inOrder.verify(firstValidationListener).caseFinished(errorMethod);
		inOrder.verify(secondValidationListener).caseFinished(errorMethod);

		inOrder.verify(firstValidationListener).validationFinished();
		inOrder.verify(secondValidationListener).validationFinished();

		verifyNoMoreInteractions(firstValidationListener, secondValidationListener);
	}

	@Test
	public void test_executorService_calls_in_order() throws InterruptedException {
		// Setup
		validationCases.add(validationCaseWithSuccess);

		ExecutorService executor = Mockito.mock(ExecutorService.class);
		when(executor.awaitTermination(anyLong(), any(TimeUnit.class))).thenReturn(true);
		implementationValidator = new ImplementationValidator(executor, MAXIMUM_TEST_TIME_IN_SECONDS, validationCases, new ArrayList<>());

		// Test
		implementationValidator.validate();

		// Assert
		InOrder inOrder = inOrder(executor);
		inOrder.verify(executor).submit(any(Runnable.class));
		inOrder.verify(executor).shutdown();
		inOrder.verify(executor).awaitTermination(MAXIMUM_TEST_TIME_IN_SECONDS, TimeUnit.SECONDS);
		inOrder.verify(executor).shutdownNow();

		verifyNoMoreInteractions(executor);
	}

	@Test
	public void test_listener_errors_not_fail_success_execution() {
		// Setup
		validationCases.add(validationCaseWithSuccess);

		doThrow(new RuntimeException("startValidation catching test")).when(firstValidationListener).startValidation();
		doThrow(new RuntimeException("startCase catching test")).when(firstValidationListener).startCase(successMethod);
		doThrow(new RuntimeException("caseSuccess catching test")).when(firstValidationListener).caseSuccess(successMethod, SUCCESS_RESULT);
		doThrow(new RuntimeException("caseFinished catching test")).when(firstValidationListener).caseFinished(successMethod);
		doThrow(new RuntimeException("validationFinished catching test")).when(firstValidationListener).validationFinished();

		// Test
		implementationValidator.validate();

		// Assert
		for (ValidationListener listener : Lists.newArrayList(firstValidationListener, secondValidationListener)) {
			verify(listener).startValidation();
			verify(listener).startCase(successMethod);
			verify(listener).caseSuccess(successMethod, SUCCESS_RESULT);
			verify(listener).caseFinished(successMethod);
			verify(listener).validationFinished();
		}
	}

	@Test
	public void test_listener_errors_not_fail_failure_execution() {
		// Setup
		validationCases.add(validationCaseWithFailure);
		validationCases.add(validationCaseWithError);

		doThrow(new RuntimeException("caseFailure catching test")).when(firstValidationListener).caseFailure(failureMethod, FAILURE_EXCEPTION);
		doThrow(new RuntimeException("caseError catching test")).when(firstValidationListener).caseError(errorMethod, ERROR_EXCEPTION);

		// Test
		implementationValidator.validate();

		// Assert
		verify(firstValidationListener).caseFailure(failureMethod, FAILURE_EXCEPTION);
		verify(secondValidationListener).caseFailure(failureMethod, FAILURE_EXCEPTION);
		verify(firstValidationListener).caseError(errorMethod, ERROR_EXCEPTION);
		verify(secondValidationListener).caseError(errorMethod, ERROR_EXCEPTION);
	}

	public static Integer methodWithSuccess(@SuppressWarnings("unused") Integer implementationToValidate) {
		return SUCCESS_RESULT;
	}

	public static Integer methodWithError(@SuppressWarnings("unused") Integer implementationToValidate) {
		throw ERROR_EXCEPTION;
	}

	public static Integer methodWithFailure(@SuppressWarnings("unused") Integer implementationToValidate) {
		throw FAILURE_EXCEPTION;
	}
}
