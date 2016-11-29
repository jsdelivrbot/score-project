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

	@Autowired
	private ScoreFillerService scoreFillerService;

	public ScoreResult addScore(String team, int score) {
		Sprint nextSprint = sprintService.prepareNextSprintFor(team);
		ScoreResult scoreResult = scoreFillerService.fillScores(MoreObjects.firstNonNull(scoreResultRepository.findOne(team), new ScoreResult(team)), nextSprint.getNumber() - 1);
		scoreResult.getScores().add(new Score(nextSprint.getNumber(), getLastScore(scoreResult).getPoints() + score));
        scoreResultRepository.save(scoreResult);
		return scoreResult;
	}

    public List<ScoreResult> getAllScoresFilled() {
		Sprint sprint = sprintService.getSprint();
		return getAllScores().stream().map(scoreResult -> scoreFillerService.fillScores(scoreResult, sprint.getNumber())).collect(Collectors.toList());
	}

	public List<ScoreResult> getAllScores() {
		return Lists.newArrayList(MoreObjects.firstNonNull(scoreResultRepository.findAll(), Lists.newArrayList()));
	}

	private static Score getLastScore(ScoreResult score) {
		return score.getScores().stream().max(Comparator.comparing(Score::getSprint)).orElseThrow(RuntimeException::new);
	}
}
