package com.dojocoders.score.junit.persistence;

import com.google.common.collect.Maps;

import java.util.Map;

public class TestStaticMap implements TestPersistUnit {

    private static final Map<String, Long> SCORE_MAP = Maps.newConcurrentMap();

    @Override
    public void putScore(String team, Long result) {
        SCORE_MAP.put(team, result);
    }

    public void clear() {
        SCORE_MAP.clear();
    }

    public Map<String, Long> getAllScores() {
        return SCORE_MAP;
    }
}
