package com.dojocoders.score.validation.launcher;

import static org.fest.assertions.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ValidationCaseTest {

	@Test
	public void test_with_direct_lamba() {
		// Test
		ValidationCase<AtomicInteger> validationCase = new ValidationCase<>(this::directCase);

		// Assert
		assertThat(validationCase.getCaseDescription().getName()).isEqualTo("directCase");
		assertThat(validationCase.getCaseDescription().getDeclaringClass().getSimpleName()).isEqualTo("ValidationCaseTest");
	}

	@Test
	public void test_with_indirect_lamba() {
		// Test
		ValidationCase<AtomicInteger> validationCase = new ValidationCase<>(this::indirectCase);

		// Assert
		assertThat(validationCase.getCaseDescription().getName()).isEqualTo("indirectCase");
		assertThat(validationCase.getCaseDescription().getDeclaringClass().getSimpleName()).isEqualTo("ValidationCaseTest");
	}

	@Test
	public void test_with_method() throws NoSuchMethodException {
		// Setup
		AtomicInteger i = new AtomicInteger(0);
		Method method = this.getClass().getDeclaredMethod("directCase", AtomicInteger.class);
		ValidationCase<AtomicInteger> validationCase = new ValidationCase<>(method, this);

		// Test
		validationCase.getCaseAccessor().accept(i);

		// Assert
		assertThat(i.get()).isEqualTo(1);
	}

	@Test
	public void test_with_static_method() throws NoSuchMethodException {
		// Setup
		AtomicInteger i = new AtomicInteger(0);
		Method method = this.getClass().getDeclaredMethod("staticCase", AtomicInteger.class);
		ValidationCase<AtomicInteger> validationCase = new ValidationCase<>(method, null);

		// Test
		validationCase.getCaseAccessor().accept(i);

		// Assert
		assertThat(i.get()).isEqualTo(-1);
	}

	private void indirectCase(AtomicInteger value) {
		directCase(value);
	}

	protected void directCase(AtomicInteger value) {
		value.incrementAndGet();
	}

	protected static void staticCase(AtomicInteger value) {
		value.decrementAndGet();
	}
}
