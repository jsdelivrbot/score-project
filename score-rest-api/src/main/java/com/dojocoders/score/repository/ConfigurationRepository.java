package com.dojocoders.score.repository;

import com.dojocoders.score.model.Configuration;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ConfigurationRepository extends ElasticsearchRepository<Configuration, String> {
}
