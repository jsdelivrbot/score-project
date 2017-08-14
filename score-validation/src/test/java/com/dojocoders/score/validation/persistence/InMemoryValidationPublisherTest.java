package com.dojocoders.score.validation.persistence;

import java.util.ArrayList;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

import com.dojocoders.score.validation.persistence.pojo.ValidationResult;

public class InMemoryValidationPublisherTest {

	@Test
	public void test_publishValidation() {
		// Setup
		InMemoryValidationPublisher publisher = new InMemoryValidationPublisher();
		ValidationResult validationResult = new ValidationResult("team", 153, new ArrayList<>());

		// Test
		publisher.publishValidation(validationResult);

		// Assert
		Assertions.assertThat(publisher.getAllResults()).hasSize(1).containsKey("team");
		Assertions.assertThat(publisher.getAllResults().get("team")).isSameAs(validationResult);
		Assertions.assertThat(publisher.getTeamResult("team")).isSameAs(validationResult);
	}

	@Test
	public void test_clear() {
		// Setup
		InMemoryValidationPublisher publisher = new InMemoryValidationPublisher();
		publisher.publishValidation(new ValidationResult("team", 153, new ArrayList<>()));
		Assertions.assertThat(publisher.getAllResults()).isNotEmpty();

		// Test
		publisher.clear();

		// Assert
		Assertions.assertThat(publisher.getAllResults()).isEmpty();
	}

}
