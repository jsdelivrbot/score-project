package com.dojocoders.score.validation.launcher;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dojocoders.score.validation.listener.ValidationListener;

public class ImplementationValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImplementationValidator.class);

	private final ExecutorService threadPool;
	private final long maximumValidationTimeInSeconds;
	private final Iterable<ValidationCase> validationCases;
	private final Iterable<ValidationListener> validationListeners;

	protected ImplementationValidator(ExecutorService threadPool, long maximumValidationTimeInSeconds, List<ValidationCase> validationCases,
			List<ValidationListener> validationListeners) {
		this.threadPool = threadPool;
		this.maximumValidationTimeInSeconds = maximumValidationTimeInSeconds;
		this.validationCases = validationCases;
		this.validationListeners = validationListeners;
	}

	public void validate() {
		callListenersSafely(ValidationListener::startValidation, "startValidation");

		for (ValidationCase oneCase : validationCases) {
			threadPool.submit(() -> validateCase(oneCase));
		}

		threadPool.shutdown();
		try {
			boolean allValidationCasesExecuted = threadPool.awaitTermination(maximumValidationTimeInSeconds, TimeUnit.SECONDS);
			if (!allValidationCasesExecuted) {
				LOGGER.error("Timeout is reached before all validation cases are finished");
			}
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException during awaiting of validation cases", e);
			throw new RuntimeException(e);
		} finally {
			callListenersSafely(ValidationListener::validationFinished, "validationFinished");
			threadPool.shutdownNow();
		}
	}

	private void callListenersSafely(Consumer<ValidationListener> listenerCall, String listenerMethodName) {
		callListenersSafely(listenerCall, listenerMethodName, null);
	}

	private void validateCase(ValidationCase caseToValidate) {
		Method caseMethod = caseToValidate.getCaseDescription();

		callListenersSafely(listener -> listener.startCase(caseMethod), "startCase", caseMethod);
		try {

			Object result = caseToValidate.callValidationCase();

			callListenersSafely(listener -> listener.caseSuccess(caseMethod, result), "caseSuccess", caseMethod);
		} catch (AssertionError e) {
			callListenersSafely(listener -> listener.caseFailure(caseMethod, e), "caseFailure", caseMethod);
		} catch (Throwable e) {
			callListenersSafely(listener -> listener.caseError(caseMethod, e), "caseError", caseMethod);
		} finally {
			callListenersSafely(listener -> listener.caseFinished(caseMethod), "caseFinished", caseMethod);
		}
	}

	private void callListenersSafely(Consumer<ValidationListener> listenerCall, String listenerMethodName, Method caseInformations) {
		for (ValidationListener listener : validationListeners) {
			try {

				listenerCall.accept(listener);

			} catch (Throwable error) {
				StringBuilder errorMessage = new StringBuilder() // 
						.append("Fail to call ") //
						.append(listener.getClass().getName()) //
						.append(".") //
						.append(listenerMethodName);
				if (caseInformations != null) {
					errorMessage.append(" for case ") //
							.append(caseInformations.getDeclaringClass().getName()) //
							.append(".") //
							.append(caseInformations.getName());
				}
				LOGGER.error(errorMessage.toString(), error);
			}
		}
	}

}
