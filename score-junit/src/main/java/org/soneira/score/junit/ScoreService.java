package org.soneira.score.junit;

import org.soneira.score.junit.persistence.PersistUnit;

import java.util.Comparator;
import java.util.List;

public class ScoreService {

    private PersistUnit persistUnit;

    public ScoreService(PersistUnit persistenceUnit) {
        this.persistUnit = persistenceUnit;
    }

    public ScoreResult addScore(String team, int score) {
        ScoreResult scoreResult = persistUnit.getScore(team);
        Integer sprint = calculateNextSprint(scoreResult);
        Integer lastScore = getLastTotalScore(scoreResult);
        scoreResult.put(sprint, lastScore + score);
        persistUnit.putScore(team, scoreResult);
        return scoreResult;
    }

    public List<ScoreResult> getAllScores() {
        return persistUnit.getAllScores();
    }


    private Integer calculateNextSprint(ScoreResult score) {
        return score == null ? 1 : score.getPointMap().keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

    private Integer getLastTotalScore(ScoreResult score) {
        return score == null ? 0
                : score.getPointMap().entrySet().stream().max(Comparator.comparingInt(m -> m.getKey())).map(m -> m.getValue()).orElse(0);
    }

}
