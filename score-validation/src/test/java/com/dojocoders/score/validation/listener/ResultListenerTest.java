package com.dojocoders.score.validation.listener;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.dojocoders.score.validation.annotations.Score;
import com.dojocoders.score.validation.persistence.ValidationPublisher;
import com.dojocoders.score.validation.persistence.pojo.CaseResult;
import com.dojocoders.score.validation.persistence.pojo.CaseResult.CaseResultType;
import com.dojocoders.score.validation.persistence.pojo.ErrorCase;
import com.dojocoders.score.validation.persistence.pojo.FailureCase;
import com.dojocoders.score.validation.persistence.pojo.SuccessCase;
import com.dojocoders.score.validation.persistence.pojo.ValidationResult;
import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class ResultListenerTest {

	private static final String TEAM = "testTeam";

	private static final Method METHOD_WITH_NO_SCORE;
	private static final Method METHOD_WITH_SCORE_22;
	private static final Method METHOD_WITH_SCORE_19;
	private static final Method METHOD_WITH_SCORE_47;
	private static final Method METHOD_WITH_SCORE_33;
	static {
		try {
			METHOD_WITH_NO_SCORE = ResultListenerTest.class.getDeclaredMethod("methodWithNoScore");
			METHOD_WITH_SCORE_22 = ResultListenerTest.class.getDeclaredMethod("methodWithScore22");
			METHOD_WITH_SCORE_19 = ResultListenerTest.class.getDeclaredMethod("methodWithScore19");
			METHOD_WITH_SCORE_47 = ResultListenerTest.class.getDeclaredMethod("methodWithScore47");
			METHOD_WITH_SCORE_33 = ResultListenerTest.class.getDeclaredMethod("methodWithScore33");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Mock
	private ValidationPublisher validationPublisher;

	@Captor
	private ArgumentCaptor<ValidationResult> validationResult;

	private ResultListener persistenceListener;

	@Before
	public void setup() {
		persistenceListener = new ResultListener(validationPublisher, TEAM);
	}

	@Test
	public void test_validationResult_is_published_on_validationFinished() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_NO_SCORE);
		persistenceListener.caseSuccess(METHOD_WITH_NO_SCORE);
		persistenceListener.caseFailure(METHOD_WITH_NO_SCORE, new AssertionError());
		persistenceListener.caseError(METHOD_WITH_NO_SCORE, new RuntimeException());
		persistenceListener.caseFinished(METHOD_WITH_NO_SCORE);

		// Assert no call
		Mockito.verifyZeroInteractions(validationPublisher);

		persistenceListener.validationFinished();

		// Assert call
		verify(validationPublisher).publishValidation(validationResult.capture());
		assertThat(validationResult.getValue().getTeam()).isEqualTo(TEAM);
	}

	@Test
	public void test_nul_points_when_no_case() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.validationFinished();

		// Assert
		assertThat(persistenceListener.getSuccess()).isEmpty();
		assertThat(persistenceListener.getFailures()).isEmpty();
		assertThat(persistenceListener.getErrors()).isEmpty();
		assertThat(persistenceListener.getTotalPoints()).isZero();
		verify(validationPublisher).publishValidation(validationResult.capture());
		assertThat(validationResult.getValue().getTotalPoints()).isEqualTo(0);
		assertThat(validationResult.getValue().getCaseResults()).isEmpty();
	}

	@Test
	public void test_points_increment_and_publishing_after_success_case() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_SCORE_22);
		persistenceListener.caseSuccess(METHOD_WITH_SCORE_22);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_22);
		persistenceListener.validationFinished();

		// Assert
		assertThat(persistenceListener.getSuccess()).isEqualTo(ImmutableMap.of(METHOD_WITH_SCORE_22, 22));
		assertThat(persistenceListener.getFailures()).isEmpty();
		assertThat(persistenceListener.getErrors()).isEmpty();
		assertThat(persistenceListener.getTotalPoints()).isEqualTo(22);
		verify(validationPublisher).publishValidation(validationResult.capture());
		assertThat(validationResult.getValue().getTotalPoints()).isEqualTo(22);
		assertThat(validationResult.getValue().getCaseResults()).hasSize(1);
		assertSuccessCase(validationResult.getValue().getCaseResults().get(0), METHOD_WITH_SCORE_22, 22);
	}

	@Test
	public void test_points_correct_increment_after_multiple_success_cases() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_SCORE_22);
		persistenceListener.startCase(METHOD_WITH_SCORE_19);
		persistenceListener.caseSuccess(METHOD_WITH_SCORE_22);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_22);
		persistenceListener.caseSuccess(METHOD_WITH_SCORE_19);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_19);
		persistenceListener.validationFinished();

		// Assert
		assertThat(persistenceListener.getSuccess()).isEqualTo(ImmutableMap.of(METHOD_WITH_SCORE_22, 22, METHOD_WITH_SCORE_19, 19));
		assertThat(persistenceListener.getFailures()).isEmpty();
		assertThat(persistenceListener.getErrors()).isEmpty();
		assertThat(persistenceListener.getTotalPoints()).isEqualTo(22 + 19);
		verify(validationPublisher).publishValidation(validationResult.capture());
		assertThat(validationResult.getValue().getTotalPoints()).isEqualTo(22 + 19);
		assertThat(validationResult.getValue().getCaseResults()).hasSize(2);
		assertSuccessCase(validationResult.getValue().getCaseResults().get(0), METHOD_WITH_SCORE_22, 22);
		assertSuccessCase(validationResult.getValue().getCaseResults().get(1), METHOD_WITH_SCORE_19, 19);
	}

	@Test
	public void test_no_points_increment_after_success_case_without_score_annotation() throws Exception {
		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_NO_SCORE);
		persistenceListener.caseSuccess(METHOD_WITH_NO_SCORE);
		persistenceListener.caseFinished(METHOD_WITH_NO_SCORE);
		persistenceListener.validationFinished();

		// Assert
		assertThat(persistenceListener.getSuccess()).isEqualTo(ImmutableMap.of(METHOD_WITH_NO_SCORE, 0));
		assertThat(persistenceListener.getFailures()).isEmpty();
		assertThat(persistenceListener.getErrors()).isEmpty();
		assertThat(persistenceListener.getTotalPoints()).isEqualTo(0);
		verify(validationPublisher).publishValidation(validationResult.capture());
		assertThat(validationResult.getValue().getTotalPoints()).isEqualTo(0);
		assertThat(validationResult.getValue().getCaseResults()).hasSize(1);
		assertSuccessCase(validationResult.getValue().getCaseResults().get(0), METHOD_WITH_NO_SCORE, 0);
	}

	@Test
	public void test_no_points_increment_after_failure_case() throws Exception {
		// Setup
		AssertionError failure = new AssertionError();

		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_SCORE_22);
		persistenceListener.caseFailure(METHOD_WITH_SCORE_22, failure);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_22);
		persistenceListener.validationFinished();

		// Assert
		assertThat(persistenceListener.getSuccess()).isEmpty();
		assertThat(persistenceListener.getFailures()).isEqualTo(ImmutableMap.of(METHOD_WITH_SCORE_22, failure));
		assertThat(persistenceListener.getErrors()).isEmpty();
		assertThat(persistenceListener.getTotalPoints()).isEqualTo(0);
		verify(validationPublisher).publishValidation(validationResult.capture());
		assertThat(validationResult.getValue().getTotalPoints()).isEqualTo(0);
		assertThat(validationResult.getValue().getCaseResults()).hasSize(1);
		assertFailureCase(validationResult.getValue().getCaseResults().get(0), METHOD_WITH_SCORE_22, failure);
	}

	@Test
	public void test_no_points_increment_after_error_case() throws Exception {
		// Setup
		Throwable error = new RuntimeException();

		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_SCORE_22);
		persistenceListener.caseError(METHOD_WITH_SCORE_22, error);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_22);
		persistenceListener.validationFinished();

		// Assert
		assertThat(persistenceListener.getSuccess()).isEmpty();
		assertThat(persistenceListener.getFailures()).isEmpty();
		assertThat(persistenceListener.getErrors()).isEqualTo(ImmutableMap.of(METHOD_WITH_SCORE_22, error));
		assertThat(persistenceListener.getTotalPoints()).isEqualTo(0);
		verify(validationPublisher).publishValidation(validationResult.capture());
		assertThat(validationResult.getValue().getTotalPoints()).isEqualTo(0);
		assertThat(validationResult.getValue().getCaseResults()).hasSize(1);
		assertErrorCase(validationResult.getValue().getCaseResults().get(0), METHOD_WITH_SCORE_22, error);
	}

	@Test
	public void test_2_points_increment_after_2_success_1_failed_and_1_error_cases() throws Exception {
		// Setup
		AssertionError failure = new AssertionError();
		Throwable error = new RuntimeException();

		// Test
		persistenceListener.startValidation();
		persistenceListener.startCase(METHOD_WITH_SCORE_22);
		persistenceListener.startCase(METHOD_WITH_SCORE_19);
		persistenceListener.startCase(METHOD_WITH_SCORE_47);
		persistenceListener.startCase(METHOD_WITH_SCORE_33);
		persistenceListener.caseSuccess(METHOD_WITH_SCORE_22);
		persistenceListener.caseFailure(METHOD_WITH_SCORE_19, failure);
		persistenceListener.caseSuccess(METHOD_WITH_SCORE_47);
		persistenceListener.caseError(METHOD_WITH_SCORE_33, error);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_19);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_22);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_47);
		persistenceListener.caseFinished(METHOD_WITH_SCORE_33);
		persistenceListener.validationFinished();

		// Assert
		assertThat(persistenceListener.getSuccess()).isEqualTo(ImmutableMap.of(METHOD_WITH_SCORE_22, 22, METHOD_WITH_SCORE_47, 47));
		assertThat(persistenceListener.getFailures()).isEqualTo(ImmutableMap.of(METHOD_WITH_SCORE_19, failure));
		assertThat(persistenceListener.getErrors()).isEqualTo(ImmutableMap.of(METHOD_WITH_SCORE_33, error));
		assertThat(persistenceListener.getTotalPoints()).isEqualTo(22 + 47);
		verify(validationPublisher).publishValidation(validationResult.capture());
		assertThat(validationResult.getValue().getTotalPoints()).isEqualTo(22 + 47);
		assertThat(validationResult.getValue().getCaseResults()).hasSize(4);
		assertSuccessCase(validationResult.getValue().getCaseResults().get(0), METHOD_WITH_SCORE_22, 22);
		assertFailureCase(validationResult.getValue().getCaseResults().get(1), METHOD_WITH_SCORE_19, failure);
		assertSuccessCase(validationResult.getValue().getCaseResults().get(2), METHOD_WITH_SCORE_47, 47);
		assertErrorCase(validationResult.getValue().getCaseResults().get(3), METHOD_WITH_SCORE_33, error);
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

	@Score(33)
	private void methodWithScore33() {
	}

	private void assertSuccessCase(CaseResult caseResult, Method expectedCaseDescription, int expectedPoints) {
		assertThat(caseResult.getDescription()).isEqualTo(expectedCaseDescription);
		assertThat(caseResult.getType()).isEqualTo(CaseResultType.SUCCESS);
		assertThat(((SuccessCase) caseResult).getPoints()).isEqualTo(expectedPoints);
	}

	private void assertFailureCase(CaseResult caseResult, Method expectedCaseDescription, AssertionError failure) {
		assertThat(caseResult.getDescription()).isEqualTo(expectedCaseDescription);
		assertThat(caseResult.getType()).isEqualTo(CaseResultType.FAILURE);
		assertThat(((FailureCase) caseResult).getFailure()).isEqualTo(failure);
	}

	private void assertErrorCase(CaseResult caseResult, Method expectedCaseDescription, Throwable error) {
		assertThat(caseResult.getDescription()).isEqualTo(expectedCaseDescription);
		assertThat(caseResult.getType()).isEqualTo(CaseResultType.ERROR);
		assertThat(((ErrorCase) caseResult).getError()).isEqualTo(error);
	}

}
