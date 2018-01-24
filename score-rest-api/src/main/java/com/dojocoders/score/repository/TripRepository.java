package com.dojocoders.score.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dojocoders.score.model.trip.Trip;

public interface TripRepository extends ElasticsearchRepository<Trip, String> {
}
