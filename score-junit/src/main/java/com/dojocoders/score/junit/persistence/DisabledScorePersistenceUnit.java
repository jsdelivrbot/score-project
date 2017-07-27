package com.dojocoders.score.junit.persistence;

public class DisabledScorePersistenceUnit implements ScorePersistenceUnit {

	@Override
	public void putScore(String team, int result) {
	}

}
