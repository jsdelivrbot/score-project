package com.dojocoders.score.configuration.embedded;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.dojocoders.score.model.Configuration;
import com.dojocoders.score.repository.ConfigurationRepository;

@Repository
@Profile("embedded-persistence")
public class EmbeddedConfigurationRepository extends AbstractEmbeddedCrudRepository<Configuration, String> implements ConfigurationRepository {

	@Value("${config.id}")
    private String configurationId;

    @Value("${config.sprintTime}")
    private Integer sprintTime;

    @Value("${config.jenkinsUrl}")
    private String jenkinsUrl;

    @Value("${config.jenkinsJobName}")
    private String jenkinsJobName;

    @Value("${config.jenkinsJobToken}")
    private String jenkinsJobToken;

    @PostConstruct
    public void addEmbeddedConfiguration() {
		Configuration embeddedConfiguration = new Configuration();
		embeddedConfiguration.setMode(configurationId);
		embeddedConfiguration.setSprintTime(sprintTime);
		embeddedConfiguration.setJenkinsUrl(jenkinsUrl);
		embeddedConfiguration.setJenkinsJobName(jenkinsJobName);
		embeddedConfiguration.setJenkinsJobToken(jenkinsJobToken);
		this.save(embeddedConfiguration);
	}

	@Override
	protected String getId(Configuration entity) {
		return entity.getMode();
	}
}
