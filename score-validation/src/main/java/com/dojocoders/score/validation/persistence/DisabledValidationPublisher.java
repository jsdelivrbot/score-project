package com.dojocoders.score.validation.persistence;

import com.dojocoders.score.validation.persistence.pojo.ValidationResult;

public class DisabledValidationPublisher implements ValidationPublisher {

	@Override
	public void publishValidation(ValidationResult validationResult) {
	}

}
