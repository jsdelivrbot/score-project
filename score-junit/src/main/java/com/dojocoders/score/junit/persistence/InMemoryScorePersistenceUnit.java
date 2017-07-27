package com.dojocoders.score.junit.persistence;

import java.util.Map;

import com.google.common.collect.Maps;

public class InMemoryScorePersistenceUnit implements ScorePersistenceUnit {

	private static final Map<String, Integer> SCORE_MAP = Maps.newConcurrentMap();

	@Override
	public void putScore(String team, int result) {
		SCORE_MAP.put(team, result);
	}

	public void clear() {
		SCORE_MAP.clear();
	}

	public Map<String, Integer> getAllScores() {
		return SCORE_MAP;
	}
}
