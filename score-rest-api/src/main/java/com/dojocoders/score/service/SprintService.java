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

	public SprintTimer startSprintTimer() {
        sprintTimer.start(getPreviousCountdown());
        sprintTimer.addScheduler(this::resetCountdown);
        sprintTimer.addScheduler(this::launchJob);
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

	private int getPreviousCountdown() {
        if (sprintTimer.getTimer().getCountdown() <= 0) {
            return configurationService.getCurrentConfiguration().getSprintTime();
        } else {
            return sprintTimer.getTimer().getCountdown();
        }
	}

	private void resetCountdown() {
		int countdown = configurationService.getCurrentConfiguration().getSprintTime();
		sprintTimer.getTimer().setCountdown(countdown);
	}

	private void launchJob() {
        Configuration configuration = configurationService.getCurrentConfiguration();
		jenkinsService.launchJobJenkins(configuration.getJenkinsUrl(), configuration.getJenkinsJobName(), configuration.getJenkinsJobToken());
	}

}
