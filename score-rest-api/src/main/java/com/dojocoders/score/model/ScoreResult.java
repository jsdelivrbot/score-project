package com.dojocoders.score.model;

import com.google.common.collect.Lists;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "softgames", type = "score")
@org.springframework.data.couchbase.core.mapping.Document
public class ScoreResult {

	private final static Score FIRST_SCORE = new Score(0,0);

	@Id
	@com.couchbase.client.java.repository.annotation.Id
	private String team;

	@com.couchbase.client.java.repository.annotation.Field
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