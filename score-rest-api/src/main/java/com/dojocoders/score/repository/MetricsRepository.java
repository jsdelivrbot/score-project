package com.dojocoders.score.repository;

import com.dojocoders.score.model.Metrics;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MetricsRepository extends ElasticsearchRepository<Metrics, String> {
}
