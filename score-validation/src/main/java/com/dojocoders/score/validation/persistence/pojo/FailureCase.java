package com.dojocoders.score.validation.persistence.pojo;

import java.lang.reflect.Method;

public final class FailureCase extends CaseResult {
	private final AssertionError failure;

	public FailureCase(Method description, AssertionError failure) {
		super(description);
		this.failure = failure;
	}

	public AssertionError getFailure() {
		return failure;
	}

	@Override
	public CaseResultType getType() {
		return CaseResultType.FAILURE;
	}
}