package org.soneira.score.junit;

import static org.fest.assertions.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.soneira.score.junit.annotations.InjectImpl;

@RunWith(BlockJUnit4ClassRunner.class)
public class ScoreBlockJUnit4ClassRunnerTest {

	@Test
	public void test_collectInitializationErrors_with_correctInjections() throws InitializationError {
		new ScoreBlockJUnit4ClassRunner(CorrectImplementationsTest.class);
	}

	@Test
	public void test_collectInitializationErrors_with_injectWithNoImplementation() {
		try {
			new ScoreBlockJUnit4ClassRunner(NoImplementationTest.class);
			failBecauseExceptionWasNotThrown(InitializationError.class);
		} catch(InitializationError e) {
			assertThat(e.getCauses()).hasSize(1);
			assertThat(e.getCauses().get(0)).hasMessageContaining("IMPLEMENTATION NOT FOUND");
		}
	}

	@Test
	public void test_collectInitializationErrors_with_injectWithMultipleImplementation() {
		try {
			new ScoreBlockJUnit4ClassRunner(MultipleImplementationsTest.class);
			failBecauseExceptionWasNotThrown(InitializationError.class);
		} catch(InitializationError e) {
			assertThat(e.getCauses()).hasSize(1);
			assertThat(e.getCauses().get(0)).hasMessageContaining("MULTIPLES IMPLEMENTATIONS");
		}
	}

	@Test
	public void test_collectInitializationErrors_with_injectWithNoDefaultConstructorImplementation() {
		try {
			new ScoreBlockJUnit4ClassRunner(NotVisibleDefaultConstructorImplementationTest.class);
			failBecauseExceptionWasNotThrown(InitializationError.class);
		} catch(InitializationError e) {
			assertThat(e.getCauses()).hasSize(1);
			assertThat(e.getCauses().get(0)).hasMessageContaining("MUST CONTAINS ONLY ONE CONSTRUCTOR WITHOUT ARGUMENTS");
		}
	}

	public static class ParentForTestClass {
		@Test public void test() {}
	}

	public static interface NoImplementation {}

	public static class NoImplementationTest extends ParentForTestClass {
		@InjectImpl NoImplementation noImplementation;
	}

	public static interface OnlyOneImplementation {}

	public static interface AnotherOnlyOneImplementation {}

	public static class OneImplementation implements OnlyOneImplementation, AnotherOnlyOneImplementation {}

	public static class CorrectImplementationsTest extends ParentForTestClass {
		@InjectImpl OnlyOneImplementation onlyOneImplementation;
		@InjectImpl AnotherOnlyOneImplementation anotherOnlyOneImplementation;
	}

	public static interface MultipleImplementations {}

	public static class FirstImplementation implements MultipleImplementations {}

	public static class SecondImplementation implements MultipleImplementations {}

	public static class MultipleImplementationsTest extends ParentForTestClass {
		@InjectImpl MultipleImplementations multipleImplementations;
	}

	public static interface NotVisibleDefaultConstructorImplementation {}

	public static class PrivateDefaultConstructor implements NotVisibleDefaultConstructorImplementation {
		protected PrivateDefaultConstructor() {}
	}

	public static class NotVisibleDefaultConstructorImplementationTest extends ParentForTestClass {
		@InjectImpl NotVisibleDefaultConstructorImplementation notVisibleDefaultConstructorImplementation;
	}
}

