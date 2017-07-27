package com.dojocoders.score.junit;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.dojocoders.score.junit.annotations.Persist;
import com.dojocoders.score.junit.annotations.Score;
import com.dojocoders.score.junit.persistence.InMemoryScorePersistenceUnit;

@RunWith(ScoreJUnitRunner.class)
@Persist(InMemoryScorePersistenceUnit.class)
public class ScoreJUnitRunnerIntegrationTestValidationClass {

	protected IIntefaceToImplement intefaceImpl = new InterfaceToImplementImpl();

	public ScoreJUnitRunnerIntegrationTestValidationClass() {
		super();
	}

	@Test
	@Score(60)
	public void sum_for2and2_then4_givePoints() {
		// Setup
		int expected = 4;

		// Test
		int result = intefaceImpl.sum(2, 2);

		// Assertions
		assertThat(result).isEqualTo(expected);
	}

	@Test
	@Score(value = 100)
	public void sum_for2and2_then9_giveNoPoints() throws InterruptedException {
		// Setup
		int expected = 9;

		// Test
		int result = intefaceImpl.sum(2, 2);

		// Assertions
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void sum_without_scoreAnnotation_giveNoPoints() {
		sum_for2and2_then4_givePoints();
	}

	@Test(timeout = 1)
	@Score(value = 300)
	public void sum_withLongTreatment_giveNoPoints() throws InterruptedException {
		while (true) {
			sum_for2and2_then4_givePoints();
		}
	}

	@Test(timeout = 1000)
	@Score(value = 500)
	public void sum_withAcceptableTreatment_givePoints() throws InterruptedException {
		for (int i = 0; i < 5; ++i) {
			sum_for2and2_then4_givePoints();
		}
	}

	public interface IIntefaceToImplement {
		int sum(int x, int y);
	}

	public static class InterfaceToImplementImpl implements IIntefaceToImplement {

		@Override
		public int sum(int x, int y) {
			return x + y;
		}
	}

}
