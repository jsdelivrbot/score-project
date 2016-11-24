package com.dojocoders.score.persistence;

import java.util.List;

import com.dojocoders.score.model.ScoreResult;

public interface PersistUnit {

    void putScore(String team, ScoreResult result);

    ScoreResult getScore(String team);

    List<ScoreResult> getAllScores();
}
