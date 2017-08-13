package com.dojocoders.score.validation.listener;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import com.dojocoders.score.validation.annotations.Score;
import com.dojocoders.score.validation.persistence.ScorePublisher;
import com.google.common.collect.Sets;

public class ScorePublisherListener implements ValidationListener {

	private static final int UNDEFINED_SCORE_POINTS = 0;

	private String concernedTeam;

	private ScorePublisher scorePublisher;

	private Collection<Method> failedTests;

	private AtomicInteger totalPoints;

	public ScorePublisherListener(ScorePublisher scorePublisher, String concernedTeam) {
		this.scorePublisher = scorePublisher;
		this.concernedTeam = concernedTeam;
	}

	public void startValidation() {
		this.failedTests = Sets.newConcurrentHashSet();
		this.totalPoints = new AtomicInteger(0);
	}

	public void startCase(Method caseDescription) {
	}

	public void caseSuccess(Method caseDescription) {
	}

	public void caseFailure(Method caseDescription, AssertionError failure) {
		failedTests.add(caseDescription);
	}

	public void caseError(Method caseDescription, Throwable error) {
		failedTests.add(caseDescription);
	}

	public void caseFinished(Method caseDescription) {
		if (!failedTests.contains(caseDescription)) {
			int testScore = getPointsActualTest(caseDescription);
			totalPoints.addAndGet(testScore);
		}
	}

	public void validationFinished() {
		scorePublisher.putScore(concernedTeam, totalPoints.get());
	}

	private int getPointsActualTest(Method caseDescription) {
		Score score = caseDescription.getAnnotation(Score.class);
		return score != null ? score.value() : UNDEFINED_SCORE_POINTS;
	}

}
