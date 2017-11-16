package com.dojocoders.score.validation.listener;

import java.lang.reflect.Method;

public abstract class AbstractValidationListener implements ValidationListener {

	@Override
	public void startValidation() {
		// Nothing by default
	}

	@Override
	public void startCase(Method caseDescription) {
		// Nothing by default
	}

	@Override
	public void caseSuccess(Method caseDescription, Object result) {
		// Nothing by default
	}

	@Override
	public void caseFailure(Method caseDescription, AssertionError failure) {
		// Nothing by default
	}

	@Override
	public void caseError(Method caseDescription, Throwable error) {
		// Nothing by default
	}

	@Override
	public void caseFinished(Method caseDescription) {
		// Nothing by default
	}

	@Override
	public void validationFinished() {
		// Nothing by default
	}

}
