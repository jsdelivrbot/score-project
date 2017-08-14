package com.dojocoders.score.validation.persistence.pojo;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class ValidationResult {
	private String team;
	private int totalPoints;
	private List<CaseResult> caseResults;

	public ValidationResult(String team, int totalPoints, List<CaseResult> caseResults) {
		this.team = team;
		this.totalPoints = totalPoints;
		this.caseResults = ImmutableList.copyOf(caseResults);
	}

	public String getTeam() {
		return team;
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public List<CaseResult> getCaseResults() {
		return caseResults;
	}
}
