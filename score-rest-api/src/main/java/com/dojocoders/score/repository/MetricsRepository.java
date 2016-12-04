package com.dojocoders.score.repository;

import com.dojocoders.score.model.Metrics;

import org.springframework.data.repository.CrudRepository;

public interface MetricsRepository extends CrudRepository<Metrics, String> {
}
