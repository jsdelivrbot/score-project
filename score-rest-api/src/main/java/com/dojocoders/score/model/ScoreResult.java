package com.dojocoders.score.model;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import com.google.common.collect.Lists;
import org.springframework.data.couchbase.core.mapping.Document;

import java.util.List;

@Document
public class ScoreResult {

	private final static Score FIRST_SCORE = new Score(0,0);

	@Id
	private String team;

	@Field
	private List<Score> scores = Lists.newArrayList(FIRST_SCORE);

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