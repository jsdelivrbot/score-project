package org.soneira.score.junit;

import org.soneira.score.junit.model.Score;
import org.soneira.score.junit.model.ScoreResult;
import org.soneira.score.junit.persistence.PersistUnit;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ScoreService {

    private PersistUnit persistUnit;

    public ScoreService(PersistUnit persistenceUnit) {
        this.persistUnit = persistenceUnit;
    }

    public ScoreResult addScore(String team, int score) {
        ScoreResult scoreResult = persistUnit.getScore(team);
        Integer lastScore = getLastTotalScore(scoreResult);
        scoreResult.put(new Date(), lastScore + score);
        persistUnit.putScore(team, scoreResult);
        return scoreResult;
    }

    public List<ScoreResult> getAllScores() {
        return persistUnit.getAllScores();
    }

    private Integer getLastTotalScore(ScoreResult score) {
        return score == null ? 0
                : score.getScores().stream().max(Comparator.comparing(Score::getSprint)).map(Score::getPoints).orElse(0);
    }

}
