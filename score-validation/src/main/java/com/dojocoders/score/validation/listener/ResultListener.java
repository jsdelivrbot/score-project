package com.dojocoders.score.validation.listener;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.dojocoders.score.validation.annotations.Score;
import com.dojocoders.score.validation.persistence.ValidationPublisher;
import com.dojocoders.score.validation.persistence.pojo.CaseResult;
import com.dojocoders.score.validation.persistence.pojo.ErrorCase;
import com.dojocoders.score.validation.persistence.pojo.FailureCase;
import com.dojocoders.score.validation.persistence.pojo.SuccessCase;
import com.dojocoders.score.validation.persistence.pojo.ValidationResult;
import com.google.common.collect.Ordering;

public class ResultListener implements ValidationListener {

	private static final int UNDEFINED_SCORE_ANNOTATION_POINTS = 0;

	private final String concernedTeam;

	private final ValidationPublisher validationPublisher;

	private final AtomicInteger totalPoints = new AtomicInteger(0);

	private final Map<Method, Integer> successCases = new ConcurrentHashMap<>();

	private final Map<Method, AssertionError> failureCases = new ConcurrentHashMap<>();

	private final Map<Method, Throwable> errorCases = new ConcurrentHashMap<>();

	private final List<Method> allCasesInStartedOrder = new CopyOnWriteArrayList<>();

	public ResultListener(ValidationPublisher validationPublisher, String concernedTeam) {
		this.validationPublisher = validationPublisher;
		this.concernedTeam = concernedTeam;
	}

	public Map<Method, AssertionError> getFailures() {
		return failureCases;
	}

	public Map<Method, Throwable> getErrors() {
		return errorCases;
	}

	public Map<Method, Integer> getSuccess() {
		return successCases;
	}

	public int getTotalPoints() {
		return totalPoints.get();
	}

	@Override
	public void startValidation() {
		this.successCases.clear();
		this.failureCases.clear();
		this.errorCases.clear();
		this.allCasesInStartedOrder.clear();
		this.totalPoints.set(0);
	}

	@Override
	public void startCase(Method caseDescription) {
		allCasesInStartedOrder.add(caseDescription);
	}

	@Override
	public void caseSuccess(Method caseDescription) {
		int caseSuccessPoints = getCasePoints(caseDescription);
		totalPoints.addAndGet(caseSuccessPoints);
		successCases.put(caseDescription, caseSuccessPoints);
	}

	private int getCasePoints(Method caseDescription) {
		Score scoreAnnotation = caseDescription.getAnnotation(Score.class);
		return scoreAnnotation != null ? scoreAnnotation.value() : UNDEFINED_SCORE_ANNOTATION_POINTS;
	}

	@Override
	public void caseFailure(Method caseDescription, AssertionError failure) {
		failureCases.put(caseDescription, failure);
	}

	@Override
	public void caseError(Method caseDescription, Throwable error) {
		errorCases.put(caseDescription, error);
	}

	@Override
	public void caseFinished(Method caseDescription) {
		// not used here
	}

	@Override
	public void validationFinished() {
		Stream<SuccessCase> successAsCaseResult = successCases.entrySet().stream().map(entry -> new SuccessCase(entry.getKey(), entry.getValue()));
		Stream<FailureCase> failuresAsCaseResult = failureCases.entrySet().stream().map(entry -> new FailureCase(entry.getKey(), entry.getValue()));
		Stream<ErrorCase> errorsAsCaseResult = errorCases.entrySet().stream().map(entry -> new ErrorCase(entry.getKey(), entry.getValue()));

		Comparator<CaseResult> startedOrder = Comparator.comparing(CaseResult::getDescription, Ordering.explicit(allCasesInStartedOrder));

		List<CaseResult> orderedList = Stream.of(successAsCaseResult, failuresAsCaseResult, errorsAsCaseResult).flatMap(Function.identity()) //
				.sorted(startedOrder).collect(Collectors.toList());

		ValidationResult result = new ValidationResult(concernedTeam, totalPoints.get(), orderedList);
		validationPublisher.publishValidation(result);
	}

}
