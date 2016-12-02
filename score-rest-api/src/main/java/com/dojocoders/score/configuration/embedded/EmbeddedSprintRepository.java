package com.dojocoders.score.configuration.embedded;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.dojocoders.score.configuration.EmbeddedConfiguration;
import com.dojocoders.score.model.Sprint;
import com.dojocoders.score.repository.SprintRepository;

@Repository
@Profile("embedded-persistence")
public class EmbeddedSprintRepository extends EmbeddedConfiguration<Sprint, String> implements SprintRepository {

	@Override
	protected String getId(Sprint entity) {
		return entity.getGameName();
	}
}
