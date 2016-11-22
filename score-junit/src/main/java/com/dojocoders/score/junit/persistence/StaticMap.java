package com.dojocoders.score.junit.persistence;

import com.dojocoders.score.junit.model.ScoreResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class StaticMap implements PersistUnit {

    private static final Map<String, ScoreResult> SCORE_MAP = Maps.newConcurrentMap();

    @Override
    public void putScore(String team, ScoreResult result) {
        SCORE_MAP.put(team, result);
    }

    @Override
    public ScoreResult getScore(String team) {
        return  SCORE_MAP.containsKey(team)? SCORE_MAP.get(team) : new ScoreResult(team);
    }


    @Override
    public List<ScoreResult> getAllScores() {
        return Lists.newArrayList(SCORE_MAP.values());
    }

    public void clear() {
        SCORE_MAP.clear();
    }
}
