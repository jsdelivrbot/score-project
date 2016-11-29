package com.dojocoders.score.service;

import com.dojocoders.score.model.Score;
import com.dojocoders.score.model.ScoreResult;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ScoreFillerService {

    public ScoreResult fillScores(ScoreResult scoreResult, int lastGlobalSprint) {
        ScoreResult newScoreResult = new ScoreResult(scoreResult.getTeam());
        newScoreResult.getScores().addAll(createAndCompleteScoreResults(scoreResult.getScores(), lastGlobalSprint));

        return newScoreResult;
    }

    private List<Score> createAndCompleteScoreResults(List<Score> scores, Integer globalLastSprint) {

        List<Score> newScoreResult = Lists.newArrayList();

        IntStream.range(1, globalLastSprint + 1).forEach(sprint -> {
            Score newScore = new Score(sprint, scores.stream().filter(score -> score.getSprint() <= sprint)
                    .sorted(Comparator.comparing(Score::getSprint).reversed()).findFirst().map(Score::getPoints).orElse(0));
            newScoreResult.add(newScore);
        });

        return newScoreResult;
    }
}
