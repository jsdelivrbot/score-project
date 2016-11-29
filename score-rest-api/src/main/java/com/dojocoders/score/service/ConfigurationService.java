package com.dojocoders.score.service;

import com.dojocoders.score.model.Configuration;
import com.dojocoders.score.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;

@Service
public class ConfigurationService {

    private static final String SPRINT_TIME = "sprintTime";
    private static final String JENKINS_URL = "jenkinsUrl";
    private static final String JENKINS_JOB_NAME = "jenkinsJobName";
    private static final String JENKINS_JOB_TOKEN = "jenkinsJobToken";

    @Autowired
    private ConfigurationRepository repository;

    public Configuration getConfiguration(String mode) {
        return repository.findOne(mode);
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
