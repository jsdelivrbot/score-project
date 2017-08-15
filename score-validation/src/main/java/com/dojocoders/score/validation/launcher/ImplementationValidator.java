package com.dojocoders.score.validation.launcher;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dojocoders.score.validation.listener.ValidationListener;

public class ImplementationValidator<Implementation> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImplementationValidator.class);

	private Iterable<ValidationListener> validationListeners;
	private Implementation implementation;
	private ExecutorService threadPool;

	public ImplementationValidator(Implementation implementation, ExecutorService threadPool, ValidationListener... validationListeners) {
		this.implementation = implementation;
		this.validationListeners = Arrays.asList(validationListeners);
		this.threadPool = threadPool;
	}

	public void validate(Iterable<ValidationCase<Implementation>> cases, long maximumTimeInSeconds) {
		callListenersSafely("startValidation", ValidationListener::startValidation);

		cases.forEach(oneCase -> threadPool.submit(() -> validateCase(oneCase)));

		threadPool.shutdown();
		try {
			boolean allValidationCasesExecuted = threadPool.awaitTermination(maximumTimeInSeconds, TimeUnit.SECONDS);
			if (!allValidationCasesExecuted) {
				LOGGER.error("Timeout is reached before all validation cases are finished");
			}
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException during awaiting of validation cases", e);
			throw new RuntimeException(e);
		} finally {
			callListenersSafely("validationFinished", ValidationListener::validationFinished);
			threadPool.shutdownNow();
		}
	}

	private void validateCase(ValidationCase<Implementation> oneCase) {
		Method caseMethod = oneCase.getCaseDescription();

		callListenersSafely("startCase", ValidationListener::startCase, caseMethod);
		try {

			oneCase.getCaseAccessor().accept(implementation);

			callListenersSafely("caseSuccess", ValidationListener::caseSuccess, caseMethod);
		} catch (AssertionError e) {
			callListenersSafely("caseFailure", listener -> listener.caseFailure(caseMethod, e), caseMethod);
		} catch (Throwable e) {
			callListenersSafely("caseError", listener -> listener.caseError(caseMethod, e), caseMethod);
		} finally {
			callListenersSafely("caseFinished", ValidationListener::caseFinished, caseMethod);
		}
	}

	private void callListenersSafely(String listenerMethodName, Consumer<ValidationListener> listenerCall) {
		validationListeners.forEach(wrapListenerExecution(listener -> listenerCall.accept(listener), Optional.empty(), listenerMethodName));
	}

	private void callListenersSafely(String listenerMethodName, Consumer<ValidationListener> listenerCall, Method caseInformations) {
		validationListeners.forEach(wrapListenerExecution(listener -> listenerCall.accept(listener), Optional.of(caseInformations), listenerMethodName));
	}

	private void callListenersSafely(String listenerMethodName, BiConsumer<ValidationListener, Method> listenerCall, Method caseInformations) {
		validationListeners.forEach(wrapListenerExecution(listener -> listenerCall.accept(listener, caseInformations), Optional.of(caseInformations), listenerMethodName));
	}

	private Consumer<ValidationListener> wrapListenerExecution(Consumer<ValidationListener> listenerCall, Optional<Method> caseInformations, String listenerMethodName) {
		return listener -> {
			try {
				listenerCall.accept(listener);
			} catch (RuntimeException error) {
				String caseInfos = caseInformations.isPresent() ? " for case " + caseInformations.get().getDeclaringClass() + "." + caseInformations.get().getName() : "";
				LOGGER.error("Fail to call " + listener.getClass().getName() + "." + listenerMethodName + caseInfos, error);
			}
		};
	}
}
