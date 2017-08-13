package com.dojocoders.score.validation.listener;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;

public class LoggerListener implements ValidationListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerListener.class);

	private Stopwatch totalElapsedTime;
	private Stopwatch combinedCasesElapsedTime;
	private final CombinedTimeTicker combinedCasesElapsedTimeTicker = new CombinedTimeTicker();
	private final ThreadLocal<Stopwatch> caseElapsedTime = new ThreadLocal<Stopwatch>();

	public void startValidation() {
		LOGGER.info("Start validation");
		totalElapsedTime = Stopwatch.createStarted();
		combinedCasesElapsedTimeTicker.reset();
		combinedCasesElapsedTime = Stopwatch.createStarted(combinedCasesElapsedTimeTicker);
	}

	public void startCase(Method caseDescription) {
		LOGGER.info("Start case {}.{}", caseDescription.getDeclaringClass().getName(), caseDescription.getName());
		caseElapsedTime.set(Stopwatch.createStarted());
	}

	public void caseSuccess(Method caseDescription) {
		LOGGER.info("Success for case {}.{}", caseDescription.getDeclaringClass().getName(), caseDescription.getName());
	}

	public void caseFailure(Method caseDescription, AssertionError failure) {
		LOGGER.warn("Failure for case " + caseDescription.getDeclaringClass().getName() + "." + caseDescription.getName(), failure);
	}

	public void caseError(Method caseDescription, Throwable error) {
		LOGGER.error("Error for case " + caseDescription.getDeclaringClass().getName() + "." + caseDescription.getName(), error);
	}

	public void caseFinished(Method caseDescription) {
		caseElapsedTime.get().stop();
		combinedCasesElapsedTimeTicker.addTime(caseElapsedTime.get());
		LOGGER.info("Finished case {}.{} in {}", caseDescription.getDeclaringClass().getName(), caseDescription.getName(), caseElapsedTime.get());
	}

	public void validationFinished() {
		totalElapsedTime.stop();
		LOGGER.info("End of validation. Take {}. All combined cases take {} (sequential time)", totalElapsedTime, combinedCasesElapsedTime);
	}

	public static class CombinedTimeTicker extends Ticker {

		private AtomicLong combinedTime = new AtomicLong(0);

		@Override
		public long read() {
			return combinedTime.get();
		}

		public void reset() {
			combinedTime.set(0);
		}

		public void addTime(Stopwatch elapsedTime) {
			long elapsedNanoTime = elapsedTime.elapsed(TimeUnit.NANOSECONDS);
			combinedTime.addAndGet(elapsedNanoTime);
		}
	}

}
