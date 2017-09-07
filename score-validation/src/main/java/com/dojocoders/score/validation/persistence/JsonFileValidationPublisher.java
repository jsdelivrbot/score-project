package com.dojocoders.score.validation.persistence;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

import com.dojocoders.score.infra.validation.JsonCaseResult;
import com.dojocoders.score.validation.persistence.pojo.CaseResult;
import com.dojocoders.score.validation.persistence.pojo.ValidationResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileValidationPublisher implements ValidationPublisher {
	public static final String JSON_EXTENSION = ".json";

	private final ObjectMapper jsonMapper = new ObjectMapper();

	private File directory;

	public JsonFileValidationPublisher(File directory) {
		this.directory = directory;
	}

	@Override
	public void publishValidation(ValidationResult validationResult) {
		File validationFile = new File(directory, validationResult.getTeam() + JSON_EXTENSION);
		List<CaseResult> jsonCaseResults = validationResult.getCaseResults().stream().map(JsonCaseResult::new).collect(Collectors.toList());
		ValidationResult jsonValidationResult = new ValidationResult(validationResult.getTeam(), validationResult.getTotalPoints(), jsonCaseResults);

		try {
			jsonMapper.writeValue(validationFile, jsonValidationResult);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}
}
