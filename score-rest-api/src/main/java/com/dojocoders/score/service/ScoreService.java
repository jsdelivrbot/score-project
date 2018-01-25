package com.dojocoders.score.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dojocoders.score.model.Score;
import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.model.Sprint;
import com.dojocoders.score.repository.ScoreResultRepository;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

@Service
public class ScoreService {

	@Autowired
	private ScoreResultRepository scoreResultRepository;

	@Autowired
	private SprintService sprintService;

	@Autowired
	private ScoreFillerService scoreFillerService;

	public ScoreResult addScore(ScoreResult scoreResult) {
		return scoreResultRepository.save(scoreResult);
	}

	public ScoreResult getScore(String team) {
		return scoreResultRepository.findById(team).get();
	}

	public boolean isScoreExists(String team) {
		return scoreResultRepository.findById(team).isPresent();
	}

	public void deleteScore(String team) {
		ScoreResult scoreResult = scoreResultRepository.findById(team).get();
		scoreResultRepository.delete(scoreResult);
	}

	public ScoreResult incrementScore(String team, int score) {
		Sprint nextSprint = sprintService.prepareNextSprintFor(team);
		ScoreResult scoreResult = scoreFillerService.fillScores(scoreResultRepository.findById(team).orElse(new ScoreResult(team)),
				nextSprint.getNumber() - 1);
		scoreResult.getScores().add(new Score(nextSprint.getNumber(), getLastScore(scoreResult).getPoints() + score));
		scoreResultRepository.save(scoreResult);
		return scoreResult;
	}

	public List<ScoreResult> getAllScoresFilled() {
		Sprint sprint = sprintService.getSprint();
		return getAllScores().stream().map(scoreResult -> scoreFillerService.fillScores(scoreResult, sprint.getNumber()))
				.collect(Collectors.toList());
	}

	public List<ScoreResult> getAllScores() {
		return Lists.newArrayList(MoreObjects.firstNonNull(scoreResultRepository.findAll(), Lists.newArrayList()));
	}

	private static Score getLastScore(ScoreResult score) {
		return score.getScores().stream().max(Comparator.comparing(Score::getSprint)).orElseThrow(RuntimeException::new);
	}
}
