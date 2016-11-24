package com.dojocoders.score.service;

import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.persistence.PersistUnit;
import com.google.common.collect.Lists;
import com.dojocoders.score.model.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class ScoreService {

    private final static Score FIRST_SCORE = new Score(0,0);

    @Autowired
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

    private Integer getNextSprint(ScoreResult score) {
        Integer lastTeamSprint = getLastScore(score).getSprint();

        Integer globalLastSprint = getAllScores().stream().map(ScoreService::getLastScore).max(Comparator.comparing(Score::getSprint)).orElse(FIRST_SCORE).getSprint();

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
        Integer globalLastSprint = getAllScores().stream().map(ScoreService::getLastScore).max(Comparator.comparing(Score::getSprint)).orElse(FIRST_SCORE).getSprint();
        List<ScoreResult> allScoresNew = Lists.newArrayList();

        getAllScores().stream().forEach(scoreResult -> {
            ScoreResult newScoreResult = new ScoreResult(scoreResult.getTeam());
            newScoreResult.getScores().addAll(createAndCompleteScoreResults(scoreResult.getScores(), globalLastSprint));
            allScoresNew.add(newScoreResult);
        });

        return allScoresNew;
    }

    public List<ScoreResult> getAllScores() {
        return persistUnit.getAllScores();
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
