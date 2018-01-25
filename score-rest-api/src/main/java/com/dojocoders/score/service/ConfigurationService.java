package com.dojocoders.score.service;

import java.security.InvalidParameterException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dojocoders.score.model.Configuration;
import com.dojocoders.score.repository.ConfigurationRepository;

@Service
public class ConfigurationService {

	private static final String SPRINT_TIME = "sprintTime";
	private static final String JENKINS_URL = "jenkinsUrl";
	private static final String JENKINS_JOB_NAME = "jenkinsJobName";
	private static final String JENKINS_JOB_TOKEN = "jenkinsJobToken";

	@Value("${config.id}")
	private String currentConfigurationId;

	@Autowired
	private ConfigurationRepository repository;

	public Configuration getCurrentConfiguration() {
		return getConfiguration(currentConfigurationId);
	}

	public Configuration getConfiguration(String mode) {
		return repository.findById(mode).orElse(null);
	}

	public Configuration setConfiguration(Configuration conf) {
		return repository.save(conf);
	}

	public Configuration updateConfiguration(String mode, String key, String value) {
		Configuration conf = getConfiguration(mode);

		switch (key) {
		case SPRINT_TIME:
			conf.setSprintTime(Integer.valueOf(value));
			break;
		case JENKINS_URL:
			conf.setJenkinsUrl(value);
			break;
		case JENKINS_JOB_NAME:
			conf.setJenkinsJobName(value);
			break;
		case JENKINS_JOB_TOKEN:
			conf.setJenkinsJobToken(value);
			break;
		default:
			throw new InvalidParameterException("Unknown key " + key);
		}

		return repository.save(conf);
	}

}
