package com.dojocoders.score.datainitialization;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.dojocoders.score.api.ScoreController;
import com.dojocoders.score.api.TeamController;

@Component
@Profile("example")
public class ExampleDataInitializer {

	@Autowired
	private TeamController teamController;

	@Autowired
	private ScoreController scoreController;

	@PostConstruct
	public void init() {
		teamController.delete();
		setupTeam("New England Patriots");
		setupTeam("Green Bay Packers");
		setupTeam("Pittsburgh Steelers");
		setupTeam("Dallas Cowboys");
		setupTeam("Philadelphia Snakes");
	}

	private void setupTeam(String name) {
		teamController.addTeam(name);
		scoreController.addScoreTeamPoints(name, name.length());
	}

}
