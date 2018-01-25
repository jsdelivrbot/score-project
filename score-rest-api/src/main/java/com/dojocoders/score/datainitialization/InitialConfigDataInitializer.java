package com.dojocoders.score.datainitialization;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dojocoders.score.model.Configuration;
import com.dojocoders.score.service.ConfigurationService;

@Component
public class InitialConfigDataInitializer {

	@Autowired
	private ConfigurationService configurationService;

	@Value("${config.id}")
	private String currentConfigurationId;

	@Value("${config.initial.jenkinsUrl}")
	private String initialJenkinsUrl;

	@Value("${config.initial.jenkinsJobName}")
	private String initialJobName;

	@Value("${config.initial.jenkinsJobToken}")
	private String initialJobToken;

	@Value("${config.initial.sprintTime}")
	private int initialSprintTime;

	@PostConstruct
	public void createCurrentConfigIfNeeded() {
		Configuration currentConfig = configurationService.getCurrentConfiguration();
		if (currentConfig == null) {
			currentConfig = new Configuration();
			currentConfig.setMode(currentConfigurationId);
			currentConfig.setJenkinsUrl(initialJenkinsUrl);
			currentConfig.setJenkinsJobName(initialJobName);
			currentConfig.setJenkinsJobToken(initialJobToken);
			currentConfig.setSprintTime(initialSprintTime);
			configurationService.setConfiguration(currentConfig);
		}
	}

}
