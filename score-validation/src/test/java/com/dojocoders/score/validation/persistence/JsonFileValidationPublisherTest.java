package com.dojocoders.score.validation.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.dojocoders.score.validation.persistence.pojo.ValidationResult;

public class JsonFileValidationPublisherTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test_publishValidation() throws IOException {
		// Setup
		File directory = folder.newFolder("folder");
		JsonFileValidationPublisher publisher = new JsonFileValidationPublisher(directory);
		ValidationResult validationResult = new ValidationResult("myTeam", 431, new ArrayList<>());

		// Test
		publisher.publishValidation(validationResult);

		// Assert
		File createdFile = new File(directory, "myTeam.json");
		assertThat(createdFile).exists().hasContent("{\"team\":\"myTeam\",\"totalPoints\":431,\"caseResults\":[]}");
	}

}
