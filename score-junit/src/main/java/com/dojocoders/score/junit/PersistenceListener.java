package com.dojocoders.score.junit;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.dojocoders.score.junit.annotations.Score;
import com.dojocoders.score.junit.config.PersistenceConfiguration;
import com.dojocoders.score.junit.persistence.ScorePersistenceUnit;
import com.google.common.collect.Sets;

public class PersistenceListener extends RunListener {

	private static final int UNDEFINED_SCORE_POINTS = 0;

	private final PersistenceConfiguration persistenceConfiguration;

	private final ScorePersistenceUnit scorePersistenceUnit;

	private final Collection<String> failedTests;

	private AtomicInteger totalPoints;

	public PersistenceListener(ScorePersistenceUnit scorePersistenceUnit, PersistenceConfiguration persistenceConfiguration) {
		this.scorePersistenceUnit = scorePersistenceUnit;
		this.persistenceConfiguration = persistenceConfiguration;
		this.failedTests = Sets.newConcurrentHashSet();
		this.totalPoints = new AtomicInteger();
	}

	@Override
	public void testIgnored(Description description) {
		failedTests.add(computeId(description));
	}

	@Override
	public void testFailure(Failure failure) {
		failedTests.add(computeId(failure.getDescription()));
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		failedTests.add(computeId(failure.getDescription()));
	}

	@Override
	public void testFinished(Description description) {
		if (!failedTests.contains(computeId(description))) {
			int testScore = getPointsActualTest(description);
			totalPoints.addAndGet(testScore);
		}
	}

	@Override
	public void testRunFinished(Result result) {
		scorePersistenceUnit.putScore(persistenceConfiguration.getTeam(), totalPoints.get());
	}

	private String computeId(Description description) {
		return description.getClassName() + '.' + description.getMethodName();
	}

	private int getPointsActualTest(Description description) {
		Score score = description.getAnnotation(Score.class);
		return score != null ? score.value() : UNDEFINED_SCORE_POINTS;
	}

}
