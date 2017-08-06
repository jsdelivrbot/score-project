package com.dojocoders.score.validation.persistence;

import java.util.Collections;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

public class InMemoryScorePublisherTest {

	@Test
	public void test_putScore() {
		// Setup
		InMemoryScorePublisher publisher = new InMemoryScorePublisher();

		// Test
		publisher.putScore("team", 153);

		// Assert
		Assertions.assertThat(publisher.getAllScores()).isEqualTo(Collections.singletonMap("team", 153));
	}

	@Test
	public void test_clear() {
		// Setup
		InMemoryScorePublisher publisher = new InMemoryScorePublisher();
		publisher.putScore("team", 153);
		Assertions.assertThat(publisher.getAllScores()).isNotEmpty();

		// Test
		publisher.clear();

		// Assert
		Assertions.assertThat(publisher.getAllScores()).isEmpty();
	}

}
