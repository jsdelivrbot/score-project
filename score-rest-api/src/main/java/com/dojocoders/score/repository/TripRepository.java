package com.dojocoders.score.repository;

import org.springframework.data.repository.CrudRepository;

import com.dojocoders.score.model.trip.Trip;

public interface TripRepository extends CrudRepository<Trip, String> {
}
