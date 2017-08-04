package com.dojocoders.score.validation.listener;

import java.lang.reflect.Method;

public interface ValidationListener {

	void startValidation();

	void startCase(Method caseDescription);

	void caseFailure(Method caseDescription);

	void caseFinished(Method caseDescription);

	void validationFinished();

}