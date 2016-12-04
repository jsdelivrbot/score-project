package com.dojocoders.score.api;

import com.dojocoders.score.model.Metrics;
import com.dojocoders.score.service.MetricsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin
public class MetricsController {

	@Autowired
	private MetricsService metricsService;

	@RequestMapping("/{team}")
	public Metrics getMetrics(@PathVariable String team) {
		return metricsService.getMetrics(team);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void updateMetrics(@RequestBody Metrics metrics) {
		metricsService.addOrUpdateMetrics(metrics);
	}

	@RequestMapping(path= "/{team}", method = RequestMethod.DELETE)
	public void clearMetrics(@PathVariable String team) {
		metricsService.clearMetrics(team);
	}

	@RequestMapping(value = "/{team}/{metric}/{value}", method = RequestMethod.PATCH)
	public void updateMetric(@PathVariable String team, @PathVariable String metric, @PathVariable String value) {
		metricsService.addOrUpdateMetric(team, metric, value);
	}

}
