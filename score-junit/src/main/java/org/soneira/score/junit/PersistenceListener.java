package org.soneira.score.junit;

import org.soneira.score.junit.annotations.Score;
import org.soneira.score.junit.persistence.PersistUnit;
import com.google.common.collect.Lists;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.TestTimedOutException;

import java.util.List;

public class PersistenceListener extends RunListener {

	private final static String DEFAULT_TEAM = "TOTO_TEAM";

	private final String team;

	private final ScoreService scoreService;

	private final List<String> startedTests;

	// FIXME si c'est en parallele???
	private int totalPoints = 0;

	public PersistenceListener(PersistUnit persistenceUnit) {
		this.scoreService = new ScoreService(persistenceUnit);
		this.team = System.getProperty("team") == null ? DEFAULT_TEAM : System.getProperty("team");
		this.startedTests = Lists.newArrayList();
	}

	@Override
	public void testStarted(Description description) throws Exception {
		startedTests.add(description.getMethodName());
	}

	@Override
	public void testFinished(Description description) throws Exception {
		if (startedTests.contains(description.getMethodName())) {
			int testScore = getPointsActualTest(description);
			totalPoints += testScore;
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
		scoreService.addScore(team, totalPoints);
		startedTests.clear();
		System.out.println("Total score : " + totalPoints + "\n");

	}

	private int getPointsActualTest(Description description) {
		Score score = description.getAnnotation(Score.class);
		return score == null ? 10 : score.value();
	}

}
