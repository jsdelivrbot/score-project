package com.dojocoders.score.model;

import org.springframework.data.annotation.Id;

public class Score {

	@Id
	private Integer sprint;

	private Integer points;

	@SuppressWarnings("unused")
	private Score() {
		// For deserialization
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
