package com.dojocoders.score.validation.persistence;

import com.dojocoders.score.validation.persistence.pojo.ValidationResult;

public interface ValidationPublisher {

	void publishValidation(ValidationResult validationResult);

}
