package com.dojocoders.score.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.model.trip.Trip;
import com.dojocoders.score.service.TripService;

@RestController
@RequestMapping("/api/trip")
@CrossOrigin
public class TripController {

	@Autowired
	private TripService tripService;

    @RequestMapping
    public List<Trip> trips() {
        return tripService.getTrips();
    }
	
	@RequestMapping(method = RequestMethod.POST)
	public void addTrip(@RequestBody Trip trip) {
		tripService.addTrip(trip);
	}

}
