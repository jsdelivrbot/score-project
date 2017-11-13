package com.dojocoders.score.validation.listener;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Stopwatch;

public class LoggerListenerTest {

	private LoggerListener loggerListener;

	private static Method method;

	private static Field totalElapsedTime;
	private static Field combinedCasesElapsedTime;
	private static Field caseElapsedTime;

	@BeforeClass
	public static void classSetup() throws NoSuchMethodException, NoSuchFieldException {
		method = LoggerListenerTest.class.getDeclaredMethod("method");

		totalElapsedTime = LoggerListener.class.getDeclaredField("totalElapsedTime");
		combinedCasesElapsedTime = LoggerListener.class.getDeclaredField("combinedCasesElapsedTime");
		caseElapsedTime = LoggerListener.class.getDeclaredField("caseElapsedTime");

		totalElapsedTime.setAccessible(true);
		combinedCasesElapsedTime.setAccessible(true);
		caseElapsedTime.setAccessible(true);
	}

	@Before
	public void setup() {
		loggerListener = new LoggerListener();
	}

	@Test
	public void test_loggerListener_stopwatchs() throws IllegalAccessException, InterruptedException {
		// Setup
		Stopwatch totalTime = Stopwatch.createStarted();

		// Test
		loggerListener.startValidation();
		loggerListener.startCase(method);
		Thread.sleep(100);
		loggerListener.caseSuccess(method, null);
		loggerListener.caseFinished(method);
		loggerListener.validationFinished();

		// Assert
		totalTime.stop();
		assertThat(getCombinedCasesElapsedTime()).isEqualTo(getCaseElapsedTime());
		assertThat(getTotalElapsedTime()).isGreaterThanOrEqualTo(getCaseElapsedTime());
		assertThat(getTotalElapsedTime()).isLessThanOrEqualTo(totalTime.elapsed(TimeUnit.NANOSECONDS));
	}

	@Test
	public void test_loggerListener_stopwatchs_accumulation() throws IllegalAccessException, InterruptedException {
		// Setup
		Stopwatch totalTime = Stopwatch.createStarted();

		// Test
		loggerListener.startValidation();

		loggerListener.startCase(method);
		Thread.sleep(100);
		loggerListener.caseSuccess(method, null);
		loggerListener.caseFinished(method);
		long firstCaseElapsedTime = getCaseElapsedTime();

		loggerListener.startCase(method);
		Thread.sleep(100);
		loggerListener.caseFailure(method, new AssertionError("loggerListener failure test"));
		loggerListener.caseFinished(method);
		long secondCaseElapsedTime = getCaseElapsedTime();

		loggerListener.startCase(method);
		Thread.sleep(100);
		loggerListener.caseError(method, new RuntimeException("loggerListener failure test"));
		loggerListener.caseFinished(method);
		long thirdCaseElapsedTime = getCaseElapsedTime();

		loggerListener.validationFinished();

		// Assert
		totalTime.stop();
		assertThat(getCombinedCasesElapsedTime()).isEqualTo(firstCaseElapsedTime + secondCaseElapsedTime + thirdCaseElapsedTime);
		assertThat(getTotalElapsedTime()).isGreaterThanOrEqualTo(getCaseElapsedTime());
		assertThat(getTotalElapsedTime()).isLessThanOrEqualTo(totalTime.elapsed(TimeUnit.NANOSECONDS));
	}

	private long getTotalElapsedTime() throws IllegalAccessException {
		return ((Stopwatch) totalElapsedTime.get(loggerListener)).elapsed(TimeUnit.NANOSECONDS);
	}

	private long getCombinedCasesElapsedTime() throws IllegalAccessException {
		return ((Stopwatch) combinedCasesElapsedTime.get(loggerListener)).elapsed(TimeUnit.NANOSECONDS);
	}

	private long getCaseElapsedTime() throws IllegalAccessException {
		return ((Stopwatch) ((ThreadLocal<?>) caseElapsedTime.get(loggerListener)).get()).elapsed(TimeUnit.NANOSECONDS);
	}

	protected void method() {
	}
}
