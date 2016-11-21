package org.soneira.score.junit.model;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class ScoreResult {

	private String team;

	private List<Score> scores = Lists.newArrayList();

	public ScoreResult() {
	}

	public ScoreResult(String team) {
		this.team = team;
	}

	public void put(Date sprint, Integer newScore) {
		scores.add(new Score(sprint, newScore));
	}

	public List<Score> getScores() {
		return this.scores;
	}

	public String getTeam() {
		return team;
	}

}