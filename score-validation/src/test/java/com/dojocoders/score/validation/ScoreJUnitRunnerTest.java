package com.dojocoders.score.validation;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.dojocoders.score.validation.PersistenceListener;
import com.dojocoders.score.validation.ScoreJUnitRunner;
import com.dojocoders.score.validation.annotations.Persist;
import com.dojocoders.score.validation.persistence.ScorePersistenceUnit;

@RunWith(MockitoJUnitRunner.class)
public class ScoreJUnitRunnerTest {

	@Mock
	private ScorePersistenceUnit mockedPersistUnit;

	@Before
	public void setup() {
		MockedPersistUnit.setup(mockedPersistUnit);
	}

	@Test
	public void test_persistenceListener_is_registered_at_run() throws InitializationError {
		// Setup
		RunNotifier notifier = Mockito.mock(RunNotifier.class);

		// Test
		new ScoreJUnitRunner(CorrectTestClass.class).run(notifier);

		// Assert
		verify(notifier).addListener(isA(PersistenceListener.class));
	}

	@Test
	public void test_persistenceListener_is_not_notified_when_initialization_errors_is_not_thrown() throws InitializationError {
		// Test
		new ScoreJUnitRunner(CorrectTestClass.class);

		// Assert
		verifyZeroInteractions(mockedPersistUnit);
	}

	@Test
	public void test_persistenceListener_is_notified_when_initialization_error_is_thrown() throws InitializationError {
		// Test
		try {
			new ScoreJUnitRunner(InvalidTestClass.class);
			Assertions.failBecauseExceptionWasNotThrown(InitializationError.class);
		} catch (InitializationError e) {
			// Assert
			mockedPersistUnit.putScore(anyString(), anyInt());
		}
	}

	public static class MockedPersistUnit implements ScorePersistenceUnit {

		private static ScorePersistenceUnit delegate;

		private static void setup(ScorePersistenceUnit mockedPersistUnit) {
			MockedPersistUnit.delegate = mockedPersistUnit;
		}

		@Override
		public void putScore(String team, int points) {
			delegate.putScore(team, points);
		}

	}

	@Persist(MockedPersistUnit.class)
	public static class CorrectTestClass {
		@Test
		public void test() {
		}
	}

	@Persist(MockedPersistUnit.class)
	public static class InvalidTestClass {
		private InvalidTestClass() {
		}
	}

}
