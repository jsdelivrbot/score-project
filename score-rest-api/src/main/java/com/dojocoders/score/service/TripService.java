package com.dojocoders.score.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dojocoders.score.model.trip.Trip;
import com.dojocoders.score.repository.TripRepository;
import com.google.common.collect.Lists;

@Service
public class TripService {

	@Autowired
	private TripRepository tripRepository;
	
	public List<Trip> getTrips() {
		return Lists.newArrayList(tripRepository.findAll());	
	}
	
	public Trip addTrip(Trip trip) {
		return tripRepository.save(trip);
	}
}
