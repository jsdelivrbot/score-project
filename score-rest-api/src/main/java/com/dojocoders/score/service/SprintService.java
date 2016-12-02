package com.dojocoders.score.service;

import com.dojocoders.score.model.Configuration;
import com.dojocoders.score.model.Sprint;
import com.dojocoders.score.model.SprintTimer;
import com.dojocoders.score.repository.SprintRepository;
import com.dojocoders.score.timer.SprintTimerComponent;
import com.google.common.base.MoreObjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SprintService {

	@Autowired
	private SprintRepository sprintRepository;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
    private SprintTimerComponent sprintTimer;

	@Autowired
    private JenkinsService jenkinsService;

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

	private int getSprintTime() {
		Integer sprint = getSprint().getNumber();
		Integer sprintTime = configurationService.getCurrentConfiguration().getSprintTime();
		if (sprint > 10) {
			sprintTime = Math.round(sprintTime / 2);
		}
		return sprintTime;
	}

	public SprintTimer startSprintTimer() {
        if (sprintTimer.getTimer().getCountdown() <= 0) {
            sprintTimer.start(getSprintTime());
        } else {
            sprintTimer.start(sprintTimer.getTimer().getCountdown());
        }
        Configuration configuration = configurationService.getCurrentConfiguration();
        sprintTimer.addScheduler(() -> jenkinsService.launchJobJenkins(configuration.getJenkinsUrl(), configuration.getJenkinsJobName(), configuration.getJenkinsJobToken()));
        sprintTimer.addScheduler(() -> {
            int sprinttime = getSprintTime();
            sprintTimer.getTimer().setCountdown(sprinttime);
        });
        return sprintTimer.getTimer();
	}

    public SprintTimer pauseSprintTimer() {
        sprintTimer.stop();
        sprintTimer.clearSchedulers();
        return sprintTimer.getTimer();
    }

    public SprintTimer getSprintTimer() {
        return sprintTimer.getTimer();
    }

}
