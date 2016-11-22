package org.soneira.score.junit;

import org.soneira.score.junit.model.Score;
import org.soneira.score.junit.model.ScoreResult;
import org.soneira.score.junit.persistence.PersistUnit;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ScoreService {

    private final static Score FIRST_SCORE = new Score(0,0);

    private PersistUnit persistUnit;

    public ScoreService(PersistUnit persistenceUnit) {
        this.persistUnit = persistenceUnit;
    }

    public ScoreResult addScore(String team, int score) {
        ScoreResult scoreResult = persistUnit.getScore(team);
        Score lastScore = getLastScore(scoreResult);
        Integer nextSprint = getNextSprint(scoreResult);

        IntStream.range(lastScore.getSprint() + 1, nextSprint).forEach(i -> {
            scoreResult.put(i, lastScore.getPoints());
        });
        scoreResult.put(nextSprint, lastScore.getPoints() + score);

        persistUnit.putScore(team, scoreResult);
        return scoreResult;
    }

    public List<ScoreResult> getAllScores() {
        return persistUnit.getAllScores();
    }

    private Integer getNextSprint(ScoreResult score) {
        Integer lastTeamSprint = getLastScore(score).getSprint();

        Integer globalLastSprint = persistUnit.getAllScores().stream().map(ScoreService::getLastScore).max(Comparator.comparing(Score::getSprint)).orElse(FIRST_SCORE).getSprint();

        if (lastTeamSprint.intValue() == globalLastSprint.intValue()) {
            return lastTeamSprint + 1;
        } else if (globalLastSprint > lastTeamSprint) {
            return globalLastSprint;
        } else {
            throw new RuntimeException("SOMETHING GETS VERY WRONG!");
        }
    }


    private static Score getLastScore(ScoreResult score) {
        return score == null ? FIRST_SCORE
                : score.getScores().stream().max(Comparator.comparing(Score::getSprint)).orElse(FIRST_SCORE);
    }

}
