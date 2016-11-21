package org.soneira.score.junit;

import org.junit.Ignore;
import org.soneira.score.junit.annotations.Persist;
import org.soneira.score.junit.annotations.InjectImpl;
import org.soneira.score.junit.annotations.Score;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.soneira.score.junit.persistence.Couchbase;

@RunWith(ScoreBlockJUnit4ClassRunner.class)
@Persist(Couchbase.class)
public class ScoreBlockJUnit4ClassRunnerIntegrationTest {

	@InjectImpl
	protected InterfaceToBeImplemented interfaceToBeImplemented;

	@Test
	@Score
    @Ignore
	public void checkSum_For2and2_then4() {
		// Setup
		int expected = 4;

		// Test
		int result = interfaceToBeImplemented.sum(2, 2);

		// Assertions
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test
	@Score(50)
    @Ignore
	public void checkSum_For2and2_then4_withMorePoints() {
		// Setup
		int expected = 4;

		// Test
		int result = interfaceToBeImplemented.sum(2, 2);

		// Assertions
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test(timeout = 1)
	@Score(value = 500, maxTimeOnly = true)
    @Ignore
	public void checkSum_longTreatment_KO() throws InterruptedException {
		// Setup
		int expected = 9;

		for (int i = 0; i < 100000; i++) {
			// System.out.println("EO");
		}

		// Test
		int result = interfaceToBeImplemented.sum(2, 2);

		// Assertions
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test(timeout = 1000)
	@Score(value = 500, maxTimeOnly = true)
    @Ignore
	public void checkSum_longTreatment_Ok_butWrongScore() throws InterruptedException {
		// Setup
		int expected = 9;

		// Test
		int result = interfaceToBeImplemented.sum(2, 2);

		// Assertions
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test(timeout = 1000)
	@Score(value = 500, maxTimeOnly = true)
    @Ignore
	public void checkSum_longTreatment_Ok() throws InterruptedException {
		// Setup
		int expected = 4;

		// Test
		int result = interfaceToBeImplemented.sum(2, 2);

		// Assertions
		Assertions.assertThat(result).isEqualTo(expected);
	}

	public static class ImplementationOfInterface implements InterfaceToBeImplemented {

		public int sum(int x, int y) {
			return x + y;
		}

	}

	public interface InterfaceToBeImplemented {

		int sum(int x, int y);
	}
}
