package com.dojocoders.score.validation.launcher;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

@RunWith(MockitoJUnitRunner.class)
public class ImplementationValidatorTest {

	private static final int IMPLEMENTATION = 10;

	@Mock
	private ValidationListener firstValidationListener;

	@Mock
	private ValidationListener secondValidationListener;

	@Mock
	private ValidationCase<Integer> firstValidationCase;

	@Mock
	private Consumer<Integer> firstConsumer;

	private Method firstMethod;

	@Mock
	private ValidationCase<Integer> secondValidationCase;

	@Mock
	private Consumer<Integer> secondConsumer;

	private Method secondMethod;

	private ImplementationValidator<Integer> implementationValidator;

	@Before
	public void setup() throws NoSuchMethodException {
		implementationValidator = new ImplementationValidator<>(IMPLEMENTATION, Executors.newSingleThreadExecutor(), firstValidationListener, secondValidationListener);
		firstMethod = ImplementationValidatorTest.class.getDeclaredMethod("firstMethod");
		secondMethod = ImplementationValidatorTest.class.getDeclaredMethod("secondMethod");

		when(firstValidationCase.getCaseAccessor()).thenReturn(firstConsumer);
		when(firstValidationCase.getCaseDescription()).thenReturn(firstMethod);
		when(secondValidationCase.getCaseAccessor()).thenReturn(secondConsumer);
		when(secondValidationCase.getCaseDescription()).thenReturn(secondMethod);
		doThrow(RuntimeException.class).when(secondConsumer).accept(IMPLEMENTATION);
	}

	@Test
	public void test_listener_consumer_calls_in_order() {
		// Setup
		List<ValidationCase<Integer>> validationCases = new ArrayList<>();
		validationCases.add(firstValidationCase);
		validationCases.add(secondValidationCase);

		// Test
		implementationValidator.validate(validationCases);

		// Assert
		InOrder inOrder = inOrder(firstConsumer, secondConsumer, firstValidationListener, secondValidationListener);
		inOrder.verify(firstValidationListener).startValidation();
		inOrder.verify(secondValidationListener).startValidation();

		inOrder.verify(firstValidationListener).startCase(firstMethod);
		inOrder.verify(secondValidationListener).startCase(firstMethod);
		inOrder.verify(firstConsumer).accept(IMPLEMENTATION);
		inOrder.verify(firstValidationListener).caseFinished(firstMethod);
		inOrder.verify(secondValidationListener).caseFinished(firstMethod);

		inOrder.verify(firstValidationListener).startCase(secondMethod);
		inOrder.verify(secondValidationListener).startCase(secondMethod);
		inOrder.verify(secondConsumer).accept(IMPLEMENTATION);
		inOrder.verify(firstValidationListener).caseFailure(secondMethod);
		inOrder.verify(secondValidationListener).caseFailure(secondMethod);
		inOrder.verify(firstValidationListener).caseFinished(secondMethod);
		inOrder.verify(secondValidationListener).caseFinished(secondMethod);

		inOrder.verify(firstValidationListener).validationFinished();
		inOrder.verify(secondValidationListener).validationFinished();

		verifyNoMoreInteractions(firstConsumer, secondConsumer, firstValidationListener, secondValidationListener);
	}

	@Test
	public void test_executorService_calls_in_order() throws InterruptedException {
		// Setup
		ExecutorService executor = Mockito.mock(ExecutorService.class);
		implementationValidator = new ImplementationValidator<>(IMPLEMENTATION, executor);

		// Test
		implementationValidator.validate(Collections.singleton(firstValidationCase));

		// Assert
		InOrder inOrder = inOrder(executor);
		inOrder.verify(executor).submit(any(Runnable.class));
		inOrder.verify(executor).shutdown();
		inOrder.verify(executor).awaitTermination(anyLong(), any(TimeUnit.class));
		inOrder.verify(executor).shutdownNow();

		verifyNoMoreInteractions(executor);
	}

	protected void firstMethod() {
	}

	protected void secondMethod() {
	}
}
