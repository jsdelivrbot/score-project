package com.dojocoders.score.validation.listener;

import static com.google.common.base.Throwables.propagate;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.dojocoders.score.validation.annotations.Score;
import com.dojocoders.score.validation.persistence.ScorePublisher;

@RunWith(MockitoJUnitRunner.class)
public class ScorePublisherListenerTest {

	private static final String TEAM = "testTeam";

	private static final Method METHOD_WITH_NO_SCORE;
	private static final Method METHOD_WITH_SCORE_22;
	private static final Method METHOD_WITH_SCORE_19;
	private static final Method METHOD_WITH_SCORE_47;
	static {
		try {
			METHOD_WITH_NO_SCORE = ScorePublisherListenerTest.class.getDeclaredMethod("methodWithNoScore");
			METHOD_WITH_SCORE_22 = ScorePublisherListenerTest.class.getDeclaredMethod("methodWithScore22");
			METHOD_WITH_SCORE_19 = ScorePublisherListenerTest.class.getDeclaredMethod("methodWithScore19");
			METHOD_WITH_SCORE_47 = ScorePublisherListenerTest.class.getDeclaredMethod("methodWithScore47");
		} catch (NoSuchMethodException e) {
			throw propagate(e);
		}
	}

	@Mock
	private ScorePublisher scorePublisher;

	private ValidationListener persistenceListener;

	@Before
	public void setup() {
		persistenceListener = new ScorePublisherListener(scorePublisher, TEAM);
	}

	@Test
	public void test_nul_score_when_no_test() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.validationFinished();

		// Assert
		verify(scorePublisher).putScore(TEAM, 0);
	}

	@Test
	public void test_score_increment_after_success_test() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_SCORE_22);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_22);
		persistenceListener.validationFinished();

		// Assert
		verify(scorePublisher).putScore(TEAM, 22);
	}

	@Test
	public void test_score_correct_increment_after_multiple_success_tests() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_SCORE_22);
		persistenceListener.startCase(METHOD_WITH_SCORE_19);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_22);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_19);
		persistenceListener.validationFinished();

		// Assert
		verify(scorePublisher).putScore(TEAM, 41);
	}

	@Test
	public void test_no_score_increment_after_success_test_without_score_annotation() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_NO_SCORE);
		persistenceListener.caseFinished(METHOD_WITH_NO_SCORE);
		persistenceListener.validationFinished();

		// Assert
		verify(scorePublisher).putScore(TEAM, 0);
	}

	@Test
	public void test_no_score_increment_after_failure_test() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_SCORE_22);
		persistenceListener.caseFailure(METHOD_WITH_SCORE_22);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_22);
		persistenceListener.validationFinished();

		// Assert
		verify(scorePublisher).putScore(TEAM, 0);
	}

	@Test
	public void test_2_score_increment_after_2_success_and_1_failed_test() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_SCORE_22);
		persistenceListener.startCase(METHOD_WITH_SCORE_19);
		persistenceListener.startCase(METHOD_WITH_SCORE_47);
		persistenceListener.caseFailure(METHOD_WITH_SCORE_19);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_19);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_22);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_47);
		persistenceListener.validationFinished();

		// Assert
		verify(scorePublisher).putScore(TEAM, 69);
	}

	protected void methodWithNoScore() {
	}

	@Score(22)
	private void methodWithScore22() {
	}

	@Score(19)
	private void methodWithScore19() {
	}

	@Score(47)
	private void methodWithScore47() {
	}

}
