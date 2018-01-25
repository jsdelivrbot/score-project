package com.dojocoders.score.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.google.common.collect.Lists;

@Document(indexName = "softgames", type = "score")
public class ScoreResult {

	private final static Score FIRST_SCORE = new Score(0, 0);

	@Id
	private String team;

	private List<Score> scores = Lists.newArrayList(FIRST_SCORE);

	@SuppressWarnings("unused")
	private ScoreResult() {
		// For deserialization
	}

	public ScoreResult(String team) {
		this.team = team;
	}

	public List<Score> getScores() {
		return this.scores;
	}

	public String getTeam() {
		return team;
	}

}