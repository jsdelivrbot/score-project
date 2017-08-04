package com.dojocoders.score.validation.launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dojocoders.score.validation.listener.ScorePublisherListener;
import com.dojocoders.score.validation.listener.ValidationListener;
import com.dojocoders.score.validation.persistence.ScorePublisher;
import com.google.common.collect.Lists;

public class ImplementationValidatorTest {

	@Mock
	private ScorePublisher scorePublisher;

	@Mock
	private ValidationListener firstValidationListener;

	@Mock
	private ValidationListener secondValidationListener;

	@Mock
	ValidationCase<Integer> firstValidationCase;

	@Mock
	ValidationCase<Integer> secondValidationCase;

	private ImplementationValidator<Integer> implementationValidator;

	@Before
	public void setup() {
		implementationValidator = new ImplementationValidator<>(10, Executors.newSingleThreadExecutor(), firstValidationListener, secondValidationListener);
	}

	@Test
	public void test_validate() {
		// Test
		implementationValidator.validate(Collections.singletonList(firstValidationCase));

		// Assert
		Mockito.verify(firstValidationCase.getCaseAccessor())
	}

	private void firstMethod(int i) {

	}

	private void firstMethod(int i) {

	}
}
