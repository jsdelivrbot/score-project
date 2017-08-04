package com.dojocoders.score.validation.launcher;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.dojocoders.score.validation.listener.ValidationListener;
import com.google.common.base.Throwables;

public class ImplementationValidator<Implementation> {

	private Iterable<ValidationListener> validationListeners;
	private Implementation implementation;
	private ExecutorService threadPool;

	public ImplementationValidator(Implementation implementation, ExecutorService threadPool, ValidationListener... validationListeners) {
		this.implementation = implementation;
		this.validationListeners = Arrays.asList(validationListeners);
		this.threadPool = threadPool;
	}

	public void validate(Iterable<ValidationCase<Implementation>> cases) {
		validationListeners.forEach(listener -> listener.startValidation());

		cases.forEach(oneCase -> threadPool.submit(() -> validate(oneCase)));

		threadPool.shutdown();
		try {
			threadPool.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Throwables.propagate(e);
		} finally {
			validationListeners.forEach(listener -> listener.validationFinished());
			threadPool.shutdownNow();
		}
	}

	private void validate(ValidationCase<Implementation> oneCase) {
		validationListeners.forEach(listener -> listener.startCase(oneCase.getCaseDescription()));
		try {
			oneCase.getCaseAccessor().accept(implementation);
		} catch (Throwable e) {
			validationListeners.forEach(listener -> listener.caseFailure(oneCase.getCaseDescription()));
		} finally {
			validationListeners.forEach(listener -> listener.caseFinished(oneCase.getCaseDescription()));
		}
	}
}
