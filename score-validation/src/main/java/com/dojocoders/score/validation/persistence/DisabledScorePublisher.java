package com.dojocoders.score.validation.persistence;

public class DisabledScorePublisher implements ScorePublisher {

	@Override
	public void putScore(String team, int result) {
	}

}
