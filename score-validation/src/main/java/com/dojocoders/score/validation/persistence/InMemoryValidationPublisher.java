package com.dojocoders.score.validation.persistence;

import java.util.Map;

import com.dojocoders.score.validation.persistence.pojo.ValidationResult;
import com.google.common.collect.Maps;

public class InMemoryValidationPublisher implements ValidationPublisher {

	private static final Map<String, ValidationResult> VALIDATION_RESULTS = Maps.newConcurrentMap();

	@Override
	public void publishValidation(ValidationResult validationResult) {
		VALIDATION_RESULTS.put(validationResult.getTeam(), validationResult);
	}

	public void clear() {
		VALIDATION_RESULTS.clear();
	}

	public Map<String, ValidationResult> getAllResults() {
		return VALIDATION_RESULTS;
	}

	public ValidationResult getTeamResult(String team) {
		return VALIDATION_RESULTS.get(team);
	}
}
