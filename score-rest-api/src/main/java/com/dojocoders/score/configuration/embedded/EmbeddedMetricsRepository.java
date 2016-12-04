package com.dojocoders.score.configuration.embedded;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.dojocoders.score.model.Metrics;
import com.dojocoders.score.repository.MetricsRepository;

@Repository
@Profile("embedded-persistence")
public class EmbeddedMetricsRepository extends AbstractEmbeddedCrudRepository<Metrics, String> implements MetricsRepository {

	@Override
	protected String getId(Metrics entity) {
		return entity.getTeam();
	}
}
