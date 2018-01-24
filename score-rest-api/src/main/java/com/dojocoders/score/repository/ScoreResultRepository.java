package com.dojocoders.score.repository;

import com.dojocoders.score.model.ScoreResult;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ScoreResultRepository extends ElasticsearchRepository<ScoreResult, String> {
}
