package com.dojocoders.score.validation.listener;

import java.lang.reflect.Method;

public interface ValidationListener {

	void startValidation();

	void startCase(Method caseDescription);

	void caseSuccess(Method caseDescription);

	void caseFailure(Method caseDescription, AssertionError failure);

	void caseError(Method caseDescription, Throwable error);

	void caseFinished(Method caseDescription);

	void validationFinished();

}