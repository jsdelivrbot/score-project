package com.dojocoders.score.junit;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import com.dojocoders.score.junit.persistence.TestStaticMap;

import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class ScoreBlockJUnit4ClassIntegrationTest {
	
	@Test
	public void checkRun_validationTest() {
		// Setup
		TestStaticMap staticMap = new TestStaticMap();
		staticMap.clear();

		// Test
		JUnitCore.runClasses(ScoreBlockJUnit4ClassRunnerValidationTest.class);

		// Assertions
		Map<String, Long> allScores = staticMap.getAllScores();
		assertThat(allScores).hasSize(1);
		assertThat(allScores).containsKey("default_team").containsValue(560L);

	}

}

