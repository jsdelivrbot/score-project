package com.dojocoders.score.validation;

import java.lang.annotation.Annotation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.dojocoders.score.validation.PersistenceListener;
import com.dojocoders.score.validation.annotations.Score;
import com.dojocoders.score.validation.config.PersistenceConfiguration;
import com.dojocoders.score.validation.persistence.ScorePersistenceUnit;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceListenerTest {

	private static final String TEAM = "testTeam";

	@Mock
	private PersistenceConfiguration persistenceConfiguration;

	@Mock
	private ScorePersistenceUnit scorePersistenceUnit;

	PersistenceListener persistenceListener;

	@Before
	public void setup() {
		Mockito.when(persistenceConfiguration.getTeam()).thenReturn(TEAM);
		persistenceListener = new PersistenceListener(scorePersistenceUnit, persistenceConfiguration);
	}

	@Test
	public void test_nul_score_when_no_test() throws Exception {
		// Test
		persistenceListener.testRunFinished(new Result());

		// Assert
		Mockito.verify(scorePersistenceUnit).putScore(TEAM, 0);
	}

	@Test
	public void test_score_increment_after_success_test() throws Exception {
		// Setup
		Description testDescription = buildTestDescriptionWithScore(22);

		// Test
		persistenceListener.testRunStarted(testDescription);
		persistenceListener.testStarted(testDescription);
		persistenceListener.testFinished(testDescription);
		persistenceListener.testRunFinished(new Result());

		// Assert
		Mockito.verify(scorePersistenceUnit).putScore(TEAM, 22);
	}

	@Test
	public void test_score_correct_increment_after_multiple_success_tests() throws Exception {
		// Setup
		Description firstTestDescription = buildTestDescriptionWithNameAndScore("firstTest", 22);
		Description secondTestDescription = buildTestDescriptionWithNameAndScore("secondTest", 19);

		// Test
		persistenceListener.testRunStarted(firstTestDescription);
		persistenceListener.testStarted(firstTestDescription);
		persistenceListener.testStarted(secondTestDescription);
		persistenceListener.testFinished(firstTestDescription);
		persistenceListener.testFinished(secondTestDescription);
		persistenceListener.testRunFinished(new Result());

		// Assert
		Mockito.verify(scorePersistenceUnit).putScore(TEAM, 41);
	}

	@Test
	public void test_no_score_increment_after_success_test_without_score_annotation() throws Exception {
		// Setup
		Description testDescription = buildTestDescriptionWithoutScore();

		// Test
		persistenceListener.testRunStarted(testDescription);
		persistenceListener.testStarted(testDescription);
		persistenceListener.testFinished(testDescription);
		persistenceListener.testRunFinished(new Result());

		// Assert
		Mockito.verify(scorePersistenceUnit).putScore(TEAM, 0);
	}

	@Test
	public void test_no_score_increment_after_failure_test() throws Exception {
		// Setup
		Description testDescription = buildTestDescriptionWithScore(22);

		// Test
		persistenceListener.testRunStarted(testDescription);
		persistenceListener.testStarted(testDescription);
		persistenceListener.testFailure(new Failure(testDescription, new RuntimeException()));
		persistenceListener.testFinished(testDescription);
		persistenceListener.testRunFinished(new Result());

		// Assert
		Mockito.verify(scorePersistenceUnit).putScore(TEAM, 0);
	}

	@Test
	public void test_no_score_increment_after_assumption_failure_test() throws Exception {
		// Setup
		Description testDescription = buildTestDescriptionWithScore(22);

		// Test
		persistenceListener.testRunStarted(testDescription);
		persistenceListener.testStarted(testDescription);
		persistenceListener.testAssumptionFailure(new Failure(testDescription, new RuntimeException()));
		persistenceListener.testFinished(testDescription);
		persistenceListener.testRunFinished(new Result());

		// Assert
		Mockito.verify(scorePersistenceUnit).putScore(TEAM, 0);
	}

	@Test
	public void test_no_score_increment_after_ignored_test() throws Exception {
		// Setup
		Description testDescription = buildTestDescriptionWithScore(22);

		// Test
		persistenceListener.testRunStarted(testDescription);
		persistenceListener.testStarted(testDescription);
		persistenceListener.testIgnored(testDescription);
		persistenceListener.testFinished(testDescription);
		persistenceListener.testRunFinished(new Result());

		// Assert
		Mockito.verify(scorePersistenceUnit).putScore(TEAM, 0);
	}

	private Description buildTestDescriptionWithoutScore() {
		return Description.createTestDescription(this.getClass(), "testName");
	}

	private Description buildTestDescriptionWithScore(int score) {
		return buildTestDescriptionWithNameAndScore("testName", score);
	}

	private Description buildTestDescriptionWithNameAndScore(String testName, int score) {
		return Description.createTestDescription(this.getClass(), testName, new Score() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Score.class;
			}

			@Override
			public int value() {
				return score;
			}
		});
	}

}
