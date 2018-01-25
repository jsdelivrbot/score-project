package com.dojocoders.score.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dojocoders.score.model.Metrics;
import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.service.MetricsService;
import com.dojocoders.score.service.ScoreService;

@RestController
@RequestMapping("/api/team")
@CrossOrigin
public class TeamController {

	private static final String METRICS_ID_PREFIX = "metrics-";

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
		if (scoreService.isScoreExists(team)) {
			throw new RuntimeException("Team already exists");
		}
		ScoreResult scoreResult = new ScoreResult(team);
		return scoreService.addScore(scoreResult);
	}

	@RequestMapping(value = "/{team}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String team) {
		scoreService.deleteScore(team);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void delete() {
		List<ScoreResult> scoreResults = scoreService.getAllScores();
		if (scoreResults != null) {
			for (ScoreResult scoreResult : scoreResults) {
				scoreService.deleteScore(scoreResult.getTeam());
				Metrics metrics = metricsService.getMetrics(METRICS_ID_PREFIX + scoreResult.getTeam());
				if (metrics != null) {
					metricsService.deleteMetrics(metrics);
				}
			}
		}
	}

}
