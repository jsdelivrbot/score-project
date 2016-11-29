package com.dojocoders.score.service;

import com.dojocoders.score.model.Sprint;
import com.dojocoders.score.repository.SprintRepository;
import com.google.common.base.MoreObjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SprintService {

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ConfigurationService configurationService;


    public Sprint prepareNextSprintFor(String team) {
        Sprint sprint = getSprint();

        if (sprint.getTeams().contains(team)) {
            sprint.setNumber(sprint.getNumber() + 1);
            sprint.getTeams().clear();
        }

        sprint.getTeams().add(team);
        sprintRepository.save(sprint);
        return sprint;
    }

    public Sprint getSprint() {
        return MoreObjects.firstNonNull(sprintRepository.findOne(Sprint.SPRINT_ID), new Sprint(1));
    }

    public int getSprintTime() {
        Integer sprint = getSprint().getNumber();
        Integer sprintTime = configurationService.getConfiguration(System.getProperty("mode")).getSprintTime();
        if (sprint > 10) {
            sprintTime =  Math.round(sprintTime / 2);
        }
        return sprintTime;
    }
}
