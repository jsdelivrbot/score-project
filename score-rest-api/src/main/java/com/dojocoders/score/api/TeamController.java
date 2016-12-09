package com.dojocoders.score.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dojocoders.score.model.Metrics;
import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.service.MetricsService;
import com.dojocoders.score.service.ScoreService;

@RestController
@RequestMapping("/api/team")
@CrossOrigin
public class TeamController {

	@Autowired
	private ScoreService scoreService;

	@Autowired
	private MetricsService metricsService;

	@RequestMapping
	public List<String> teams() {
		return scoreService.getAllScores().stream().map(ScoreResult::getTeam).collect(Collectors.toList());
	}

	@RequestMapping(value = "/{team}", method = RequestMethod.POST)
	public ScoreResult addTeam(@PathVariable String team) {
		if (scoreService.getScore(team) != null) {
			throw new RuntimeException("Team already exists");
		}
		ScoreResult scoreResult = new ScoreResult(team);
		return scoreService.addScore(scoreResult);
	}

	@RequestMapping(value = "/{team}/{newTeam}", method = RequestMethod.PUT)
	public ScoreResult updateTeam(@PathVariable String team, @PathVariable String newTeam) {
		ScoreResult scoreResult = scoreService.getScore(team);
		if (scoreResult == null) {
			throw new RuntimeException("Team doesn't exists");
		}
		ScoreResult newScoreResult = new ScoreResult(newTeam);
		newScoreResult.getScores().addAll(scoreResult.getScores());
		scoreService.deleteScore(team);

		Metrics metrics = metricsService.getMetrics(team + "-metrics");
		if (metrics != null) {
			Metrics newMetrics = new Metrics(team + "-metrics");
			newMetrics.getMetrics().putAll(metrics.getMetrics());

			metricsService.deleteMetrics(metrics);
			metricsService.addOrUpdateMetrics(newMetrics);
		}

		return scoreService.addScore(newScoreResult);
	}

	@RequestMapping(value = "/{team}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String team) {
		ScoreResult scoreResult = scoreService.getScore(team);
		if (scoreResult == null) {
			throw new RuntimeException("Team doesn't exists");
		}
		scoreService.deleteScore(team);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void delete() {
		List<ScoreResult> scoreResults = scoreService.getAllScores();
		if (scoreResults != null) {
			for (ScoreResult scoreResult : scoreResults) {
				scoreService.deleteScore(scoreResult.getTeam());
				Metrics metrics = metricsService.getMetrics(scoreResult.getTeam() + "-metrics");
				if (metrics != null) {
					metricsService.deleteMetrics(metrics);
				}
			}
		}
	}

}
