package org.soneira.score.junit.persistence;

import org.soneira.score.junit.model.ScoreResult;

import java.util.List;

public interface PersistUnit {

    void putScore(String team, ScoreResult result);

    ScoreResult getScore(String team);

    List<ScoreResult> getAllScores();
}
