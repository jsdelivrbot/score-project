package com.dojocoders.score.repository;

import com.dojocoders.score.model.Sprint;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SprintRepository extends ElasticsearchRepository<Sprint, String> {

}
