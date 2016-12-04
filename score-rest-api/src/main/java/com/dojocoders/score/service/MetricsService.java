package com.dojocoders.score.service;

import com.dojocoders.score.model.Metrics;
import com.dojocoders.score.repository.MetricsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    @Autowired
    private MetricsRepository repository;

    public Metrics getMetrics(String team) {
        return repository.findOne(team);
    }

    private Metrics getOrCreateMetrics(String team) {
    	Metrics metrics = repository.findOne(team);
    	if (metrics == null) {
    		metrics = new Metrics(team);
    	}
    	return metrics;
    }

    public void addOrUpdateMetrics(Metrics metrics) {
    	Metrics currentMetrics = getOrCreateMetrics(metrics.getTeam());
    	currentMetrics.getMetrics().putAll(metrics.getMetrics());
        repository.save(currentMetrics);
    }

    public void clearMetrics(String team) {
    	Metrics currentMetrics = getOrCreateMetrics(team);
    	currentMetrics.getMetrics().clear();
        repository.save(currentMetrics);
    }

    public void addOrUpdateMetric(String team, String metric, String value) {
    	Metrics currentMetrics = getOrCreateMetrics(team);
    	currentMetrics.getMetrics().put(metric, value);
        repository.save(currentMetrics);
    }

}
