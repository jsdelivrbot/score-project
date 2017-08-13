package com.dojocoders.score.validation.persistence;

import java.io.File;
import java.io.IOException;

import org.fest.assertions.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JsonFileScorePublisherTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test_putScore() throws IOException {
		// Setup
		File directory = folder.newFolder("folder");
		JsonFileScorePublisher publisher = new JsonFileScorePublisher(directory);

		// Test
		publisher.putScore("myTeam", 431);

		// Assert
		File createdFile = new File(directory, "myTeam.json");
		Assertions.assertThat(createdFile).exists().hasContent("{\"team\":\"myTeam\",\"points\":431}");
	}

}
