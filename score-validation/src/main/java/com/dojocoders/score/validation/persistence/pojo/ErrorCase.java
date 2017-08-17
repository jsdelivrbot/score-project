package com.dojocoders.score.validation.persistence.pojo;

import java.lang.reflect.Method;

public final class ErrorCase extends CaseResult {
	private final Throwable error;

	public ErrorCase(Method description, Throwable error) {
		super(description);
		this.error = error;
	}

	public Throwable getError() {
		return error;
	}

	@Override
	public CaseResultType getType() {
		return CaseResultType.ERROR;
	}

}