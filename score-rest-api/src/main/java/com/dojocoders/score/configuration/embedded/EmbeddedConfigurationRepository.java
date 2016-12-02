package com.dojocoders.score.configuration.embedded;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.dojocoders.score.configuration.EmbeddedConfiguration;
import com.dojocoders.score.model.Configuration;
import com.dojocoders.score.repository.ConfigurationRepository;

@Repository
@Profile("embedded-persistence")
public class EmbeddedConfigurationRepository extends EmbeddedConfiguration<Configuration, String> implements ConfigurationRepository {

	@Override
	protected String getId(Configuration entity) {
		return entity.getMode();
	}
}
