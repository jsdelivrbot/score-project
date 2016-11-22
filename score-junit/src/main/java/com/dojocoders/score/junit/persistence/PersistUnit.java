package com.dojocoders.score.junit.persistence;

import com.dojocoders.score.junit.model.ScoreResult;

import java.util.List;

public interface PersistUnit {

    void putScore(String team, ScoreResult result);

    ScoreResult getScore(String team);

    List<ScoreResult> getAllScores();
}
