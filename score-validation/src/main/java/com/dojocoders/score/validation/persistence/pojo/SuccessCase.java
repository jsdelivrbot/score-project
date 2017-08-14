package com.dojocoders.score.validation.persistence.pojo;

import java.lang.reflect.Method;

public final class SuccessCase extends CaseResult {
	private int points;

	public SuccessCase(Method description, int points) {
		super(description);
		this.points = points;
	}

	public int getPoints() {
		return points;
	}

	@Override
	public CaseResultType getType() {
		return CaseResultType.SUCCESS;
	}
}