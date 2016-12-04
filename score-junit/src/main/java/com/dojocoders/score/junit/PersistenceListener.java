package com.dojocoders.score.junit;

import com.dojocoders.score.junit.persistence.TestPersistUnit;
import com.dojocoders.score.junit.annotations.Score;
import com.google.common.collect.Lists;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PersistenceListener extends RunListener {

	private final String team;

	private final TestPersistUnit testPersistUnit;

	private final List<String> startedTests;

	private AtomicInteger totalPoints;

	public PersistenceListener(TestPersistUnit persistenceUnit) {
		this.testPersistUnit = persistenceUnit;
		this.team = TestConfiguration.getTeam();
		this.startedTests = Lists.newArrayList();
		this.totalPoints = new AtomicInteger();
	}

	@Override
	public void testStarted(Description description) throws Exception {
		startedTests.add(description.getMethodName());
	}

	@Override
	public void testFinished(Description description) throws Exception {
		if (startedTests.contains(description.getMethodName())) {
			int testScore = getPointsActualTest(description);
			totalPoints.addAndGet(testScore);
			System.out.println("Test finished : " + description.getMethodName() + ", score : " + testScore + "\n");
			startedTests.remove(description.getMethodName());
		}
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		startedTests.remove(failure.getDescription().getMethodName());
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		startedTests.remove(failure.getDescription().getMethodName());
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		long score = Math.round(totalPoints.get()*TestConfiguration.getScoreCoefficient()) + TestConfiguration.getBonusMalus();
		testPersistUnit.putScore(team, score);
		startedTests.clear();
		System.out.println("Total score : " + score + "\n");

	}

	private int getPointsActualTest(Description description) {
		Score score = description.getAnnotation(Score.class);
		return score == null ? TestConfiguration.DEFAULT_METHOD_POINTS : score.value();
	}

}
