package com.dojocoders.score.junit;

import com.dojocoders.score.junit.model.ScoreResult;
import com.dojocoders.score.junit.persistence.PersistUnit;
import com.google.common.collect.Lists;
import com.dojocoders.score.junit.model.Score;

import java.util.*;
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

    public List<ScoreResult> getAllScoresFilled() {
        Integer globalLastSprint = persistUnit.getAllScores().stream().map(ScoreService::getLastScore).max(Comparator.comparing(Score::getSprint)).orElse(FIRST_SCORE).getSprint();
        List<ScoreResult> allScoresNew = Lists.newArrayList();

        getAllScores().stream().forEach(scoreResult -> {
            ScoreResult newScoreResult = new ScoreResult(scoreResult.getTeam());
            newScoreResult.getScores().addAll(createAndCompleteScoreResults(scoreResult.getScores(), globalLastSprint));
            allScoresNew.add(newScoreResult);
        });

        return allScoresNew;
    }

    private List<Score> createAndCompleteScoreResults(List<Score> scores, Integer globalLastSprint) {
        List<Score> newScoreResult = Lists.newArrayList();

        IntStream.range(1, globalLastSprint+1).forEach(sprint -> {
            Score newScore = new Score(sprint, scores.stream().filter(score -> score.getSprint() <= sprint).sorted(Comparator.comparing(Score::getSprint).reversed()).findFirst().map(Score::getPoints).orElse(0));
            newScoreResult.add(newScore);
        });

        return newScoreResult;
    }
}
