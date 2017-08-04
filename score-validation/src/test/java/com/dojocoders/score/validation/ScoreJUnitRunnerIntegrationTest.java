package com.dojocoders.score.validation;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.JUnitCore;

import com.dojocoders.codingwars.validation.PersistenceConfiguration;
import com.dojocoders.score.validation.persistence.InMemoryScorePublisher;

public class ScoreJUnitRunnerIntegrationTest {

	@Test
	public void checkRun_validationTest() {
		// Setup
		InMemoryScorePublisher staticMap = new InMemoryScorePublisher();
		staticMap.clear();
		System.setProperty(PersistenceConfiguration.TEAM_PARAMETER, "myTeam");

		// Test
		JUnitCore.runClasses(ScoreJUnitRunnerIntegrationTestValidationClass.class);

		// Assertions
		Map<String, Integer> allScores = staticMap.getAllScores();
		assertThat(allScores).hasSize(1);
		assertThat(allScores).containsKey("myTeam").containsValue(560);

	}

}
