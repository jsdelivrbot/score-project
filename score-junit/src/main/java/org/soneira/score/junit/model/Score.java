package org.soneira.score.junit.model;

public class Score {

	private Integer sprint;

	private Integer points;

	public Score() {
	}

	public Score(Integer sprint, Integer points) {
		this.sprint = sprint;
		this.points = points;
	}

	public Integer getSprint() {
		return sprint;
	}

	public Integer getPoints() {
		return points;
	}
}
