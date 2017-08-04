package com.dojocoders.score.configuration.embedded;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.dojocoders.score.model.trip.Trip;
import com.dojocoders.score.repository.TripRepository;

@Repository
@Profile("embedded-persistence")
public class EmbeddedTripRepository extends AbstractEmbeddedCrudRepository<Trip, String> implements TripRepository {

	@Override
	protected String getId(Trip entity) {
		return entity.getId();
	}

}
