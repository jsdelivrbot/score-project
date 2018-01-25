package com.dojocoders.score.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.google.common.collect.Lists;

@Document(indexName = "softgames", type = "sprint")
public class Sprint {

	public static final String SPRINT_ID = "score-ihm-sprint";

	@Id
	private String gameName = SPRINT_ID;

	private Integer number;

	private List<String> teams = Lists.newArrayList();

	@SuppressWarnings("unused")
	private Sprint() {
		// For deserialization
	}

	public Sprint(Integer number) {
		this.number = number;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public List<String> getTeams() {
		return teams;
	}

	public void setTeams(List<String> teams) {
		this.teams = teams;
	}
}
