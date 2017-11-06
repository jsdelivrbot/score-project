package com.dojocoders.score.validation.persistence.pojo;

import java.lang.reflect.Method;

public abstract class CaseResult {

	public enum CaseResultType {
		SUCCESS, FAILURE, ERROR
	}

	private Method description;

	protected CaseResult(Method description) {
		this.description = description;
	}

	public Method getDescription() {
		return description;
	}

	public abstract CaseResultType getType();
}