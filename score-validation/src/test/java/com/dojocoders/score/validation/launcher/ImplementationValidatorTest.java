package com.dojocoders.score.validation.launcher;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.dojocoders.score.validation.listener.ValidationListener;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class ImplementationValidatorTest {

	private static final Integer TESTED_IMPLEMENTATION = new Integer(new Random().nextInt());

	private ImplementationValidator<Integer> implementationValidator;

	@Mock
	private ValidationListener firstValidationListener;

	@Mock
	private ValidationListener secondValidationListener;

	@Mock
	private ValidationCase<Integer> validationCaseWithSuccess;

	@Mock
	private Consumer<Integer> consumerWithSuccess;

	private Method methodWithSuccess;

	@Mock
	private ValidationCase<Integer> validationCaseWithFailure;

	@Mock
	private Consumer<Integer> consumerWithFailure;

	private Method methodWithFailure;

	private AssertionError validationCaseFailure;

	@Mock
	private ValidationCase<Integer> validationCaseWithError;

	@Mock
	private Consumer<Integer> consumerWithError;

	private Method methodWithError;

	private RuntimeException validationCaseError;

	@Before
	public void setup() throws NoSuchMethodException {
		implementationValidator = new ImplementationValidator<>(TESTED_IMPLEMENTATION, Executors.newSingleThreadExecutor(), firstValidationListener, secondValidationListener);

		methodWithSuccess = ImplementationValidatorTest.class.getDeclaredMethod("methodWithSuccess");
		when(validationCaseWithSuccess.getCaseAccessor()).thenReturn(consumerWithSuccess);
		when(validationCaseWithSuccess.getCaseDescription()).thenReturn(methodWithSuccess);

		methodWithFailure = ImplementationValidatorTest.class.getDeclaredMethod("methodWithFailure");
		when(validationCaseWithFailure.getCaseAccessor()).thenReturn(consumerWithFailure);
		when(validationCaseWithFailure.getCaseDescription()).thenReturn(methodWithFailure);
		validationCaseFailure = new AssertionError("thrown by third consumer");
		doThrow(validationCaseFailure).when(consumerWithFailure).accept(TESTED_IMPLEMENTATION);

		methodWithError = ImplementationValidatorTest.class.getDeclaredMethod("methodWithError");
		when(validationCaseWithError.getCaseAccessor()).thenReturn(consumerWithError);
		when(validationCaseWithError.getCaseDescription()).thenReturn(methodWithError);
		validationCaseError = new RuntimeException("thrown by second consumer");
		doThrow(validationCaseError).when(consumerWithError).accept(TESTED_IMPLEMENTATION);
	}

	@Test
	public void test_listener_consumer_calls_in_order() {
		// Setup
		List<ValidationCase<Integer>> validationCases = new ArrayList<>();
		validationCases.add(validationCaseWithSuccess);
		validationCases.add(validationCaseWithFailure);
		validationCases.add(validationCaseWithError);

		// Test
		implementationValidator.validate(validationCases);

		// Assert
		InOrder inOrder = inOrder(consumerWithSuccess, consumerWithError, consumerWithFailure, firstValidationListener, secondValidationListener);
		inOrder.verify(firstValidationListener).startValidation();
		inOrder.verify(secondValidationListener).startValidation();

		inOrder.verify(firstValidationListener).startCase(methodWithSuccess);
		inOrder.verify(secondValidationListener).startCase(methodWithSuccess);
		inOrder.verify(consumerWithSuccess).accept(TESTED_IMPLEMENTATION);
		inOrder.verify(firstValidationListener).caseSuccess(methodWithSuccess);
		inOrder.verify(secondValidationListener).caseSuccess(methodWithSuccess);
		inOrder.verify(firstValidationListener).caseFinished(methodWithSuccess);
		inOrder.verify(secondValidationListener).caseFinished(methodWithSuccess);

		inOrder.verify(firstValidationListener).startCase(methodWithFailure);
		inOrder.verify(secondValidationListener).startCase(methodWithFailure);
		inOrder.verify(consumerWithFailure).accept(TESTED_IMPLEMENTATION);
		inOrder.verify(firstValidationListener).caseFailure(methodWithFailure, validationCaseFailure);
		inOrder.verify(secondValidationListener).caseFailure(methodWithFailure, validationCaseFailure);
		inOrder.verify(firstValidationListener).caseFinished(methodWithFailure);
		inOrder.verify(secondValidationListener).caseFinished(methodWithFailure);

		inOrder.verify(firstValidationListener).startCase(methodWithError);
		inOrder.verify(secondValidationListener).startCase(methodWithError);
		inOrder.verify(consumerWithError).accept(TESTED_IMPLEMENTATION);
		inOrder.verify(firstValidationListener).caseError(methodWithError, validationCaseError);
		inOrder.verify(secondValidationListener).caseError(methodWithError, validationCaseError);
		inOrder.verify(firstValidationListener).caseFinished(methodWithError);
		inOrder.verify(secondValidationListener).caseFinished(methodWithError);

		inOrder.verify(firstValidationListener).validationFinished();
		inOrder.verify(secondValidationListener).validationFinished();

		verifyNoMoreInteractions(consumerWithSuccess, consumerWithError, consumerWithFailure, firstValidationListener, secondValidationListener);
	}

	@Test
	public void test_executorService_calls_in_order() throws InterruptedException {
		// Setup
		ExecutorService executor = Mockito.mock(ExecutorService.class);
		when(executor.awaitTermination(anyLong(), any(TimeUnit.class))).thenReturn(true);
		implementationValidator = new ImplementationValidator<>(TESTED_IMPLEMENTATION, executor);

		// Test
		implementationValidator.validate(Collections.singleton(validationCaseWithSuccess));

		// Assert
		InOrder inOrder = inOrder(executor);
		inOrder.verify(executor).submit(any(Runnable.class));
		inOrder.verify(executor).shutdown();
		inOrder.verify(executor).awaitTermination(anyLong(), any(TimeUnit.class));
		inOrder.verify(executor).shutdownNow();

		verifyNoMoreInteractions(executor);
	}

	@Test
	public void test_listener_errors_not_fail_success_execution() {
		// Setup
		doThrow(new RuntimeException("startValidation catching test")).when(firstValidationListener).startValidation();
		doThrow(new RuntimeException("startCase catching test")).when(firstValidationListener).startCase(any(Method.class));
		doThrow(new RuntimeException("caseSuccess catching test")).when(firstValidationListener).caseSuccess(any(Method.class));
		doThrow(new RuntimeException("caseFinished catching test")).when(firstValidationListener).caseFinished(any(Method.class));
		doThrow(new RuntimeException("validationFinished catching test")).when(firstValidationListener).validationFinished();

		// Test
		implementationValidator.validate(Collections.singleton(validationCaseWithSuccess));

		// Assert
		verify(consumerWithSuccess).accept(TESTED_IMPLEMENTATION);
		for (ValidationListener listener : Lists.newArrayList(firstValidationListener, secondValidationListener)) {
			verify(listener).startValidation();
			verify(listener).startCase(methodWithSuccess);
			verify(listener).caseSuccess(methodWithSuccess);
			verify(listener).caseFinished(methodWithSuccess);
			verify(listener).validationFinished();
		}
	}

	@Test
	public void test_listener_errors_not_fail_failure_execution() {
		// Setup
		doThrow(new RuntimeException("caseFailure catching test")).when(firstValidationListener).caseFailure(any(Method.class), any(AssertionError.class));
		doThrow(new RuntimeException("caseError catching test")).when(firstValidationListener).caseError(any(Method.class), any(Throwable.class));
		List<ValidationCase<Integer>> validationCases = new ArrayList<>();
		validationCases.add(validationCaseWithFailure);
		validationCases.add(validationCaseWithError);

		// Test
		implementationValidator.validate(validationCases);

		// Assert
		verify(consumerWithFailure).accept(TESTED_IMPLEMENTATION);
		verify(consumerWithError).accept(TESTED_IMPLEMENTATION);
		verify(firstValidationListener).caseFailure(methodWithFailure, validationCaseFailure);
		verify(secondValidationListener).caseFailure(methodWithFailure, validationCaseFailure);
		verify(firstValidationListener).caseError(methodWithError, validationCaseError);
		verify(secondValidationListener).caseError(methodWithError, validationCaseError);
	}

	protected void methodWithSuccess() {
	}

	protected void methodWithError() {
	}

	protected void methodWithFailure() {
	}
}
