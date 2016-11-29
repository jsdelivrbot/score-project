package com.dojocoders.score.service;

import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.model.Sprint;
import com.dojocoders.score.repository.ScoreResultRepository;
import com.google.common.base.*;
import com.google.common.collect.Lists;
import com.dojocoders.score.model.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ScoreService {

	@Autowired
	private ScoreResultRepository scoreResultRepository;

	@Autowired
	private SprintService sprintService;

	public ScoreResult addScore(String team, int score) {
		ScoreResult scoreResult = fillScores(MoreObjects.firstNonNull(scoreResultRepository.findOne(team), new ScoreResult(team)));
		scoreResult.getScores().add(new Score(sprintService.prepareNextSprintFor(team).getNumber(), getLastScore(scoreResult).getPoints() + score));
        scoreResultRepository.save(scoreResult);
		return scoreResult;
	}

    public List<ScoreResult> getAllScoresFilled() {
		return getAllScores().stream().map(this::fillScores).collect(Collectors.toList());
	}

	public List<ScoreResult> getAllScores() {
		return Lists.newArrayList(scoreResultRepository.findAll());
	}

	private static Score getLastScore(ScoreResult score) {
		return score.getScores().stream().max(Comparator.comparing(Score::getSprint)).orElseThrow(RuntimeException::new);
	}

    private ScoreResult fillScores(ScoreResult scoreResult) {
        Sprint sprint = sprintService.getSprint();

        ScoreResult newScoreResult = new ScoreResult(scoreResult.getTeam());
        newScoreResult.getScores().addAll(createAndCompleteScoreResults(scoreResult.getScores(), sprint.getNumber()));

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
