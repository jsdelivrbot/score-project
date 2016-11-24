package com.dojocoders.score.junit;

import static org.fest.assertions.api.Assertions.assertThat;

import com.dojocoders.score.junit.annotations.InjectImpl;
import com.dojocoders.score.junit.annotations.Persist;
import com.dojocoders.score.junit.persistence.ScoreApiRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.dojocoders.score.junit.annotations.Score;

@RunWith(ScoreBlockJUnit4ClassRunner.class)
@Persist(ScoreApiRest.class)
public class ScoreBlockJUnit4ClassRunnerValidationTest {

	@InjectImpl
	protected IIntefaceToImplement intefaceImpl;

	public ScoreBlockJUnit4ClassRunnerValidationTest() {
		super();
	}

	@Test
	@Score
	public void checkSum_For2and2_then4() {
		// Setup
		int expected = 4;

		// Test
		int result = intefaceImpl.sum(2, 2);

		// Assertions
		assertThat(result).isEqualTo(expected);
	}

	@Test
	@Score(50)
	public void checkSum_For2and2_then4_withMorePoints() {
		// Setup
		int expected = 4;

		// Test
		int result = intefaceImpl.sum(2, 2);

		// Assertions
		assertThat(result).isEqualTo(expected);
	}

	@Test(timeout = 1)
	@Score(value = 500, maxTimeOnly = true)
	public void checkSum_longTreatment_KO() throws InterruptedException {
		// Setup
		int expected = 9;

		for (int i = 0; i < 100000; i++) {
			// System.out.println("EO");
		}
		// Test
		int result = intefaceImpl.sum(2, 2);

		// Assertions
		assertThat(result).isEqualTo(expected);
	}

	@Test(timeout = 1000)
	@Score(value = 500, maxTimeOnly = true)
	public void checkSum_longTretment_OK_butWrongScore() throws InterruptedException {
		// Setup
		int expected = 9;

		// Test
		int result = intefaceImpl.sum(2, 2);

		// Assertions
		assertThat(result).isEqualTo(expected);
	}

	@Test(timeout = 1000)
	@Score(value = 500, maxTimeOnly = true)
	public void checkSum_longTretment_OK() throws InterruptedException {
		// Setup
		int expected = 4;

		// Test
		int result = intefaceImpl.sum(2, 2);

		// Assertions
		assertThat(result).isEqualTo(expected);
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
