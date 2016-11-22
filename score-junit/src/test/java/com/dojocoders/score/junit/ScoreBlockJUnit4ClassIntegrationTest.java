package com.dojocoders.score.junit;

import com.dojocoders.score.junit.model.ScoreResult;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import com.dojocoders.score.junit.persistence.StaticMap;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ScoreBlockJUnit4ClassIntegrationTest {
	
	@Test
	public void checkRun_validationTest() {
		// Setup
		StaticMap staticMap = new StaticMap();
		staticMap.clear();

		// Test
		Result result = JUnitCore.runClasses(ScoreBlockJUnit4ClassRunnerValidationTest.class);

		// Assertions
		List<ScoreResult> allScores = staticMap.getAllScores();
		assertThat(allScores).hasSize(1);
		assertThat(allScores.get(0).getTeam()).isEqualTo("default_team");
		Assertions.assertThat(allScores.get(0).getScores()).hasSize(1);
		assertThat(allScores.get(0).getScores().get(0).getPoints()).isEqualTo(560);

	}

}

