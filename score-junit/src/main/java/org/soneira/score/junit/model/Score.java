package org.soneira.score.junit.model;

import com.couchbase.client.deps.com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Score {

    public static final String JSON_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'z'";

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATE_PATTERN)
	private Date sprint;

	private Integer points;

	public Score() {
	}

	public Score(Date sprint, Integer points) {
		this.sprint = sprint;
		this.points = points;
	}

	public Date getSprint() {
		return sprint;
	}

	public Integer getPoints() {
		return points;
	}
}
