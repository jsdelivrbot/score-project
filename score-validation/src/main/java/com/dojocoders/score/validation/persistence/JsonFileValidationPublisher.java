package com.dojocoders.score.validation.persistence;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import com.dojocoders.score.validation.persistence.pojo.ValidationResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileValidationPublisher implements ValidationPublisher {
	public static final String JSON_EXTENSION = ".json";

	private File directory;

	public JsonFileValidationPublisher(File directory) {
		this.directory = directory;
	}

	@Override
	public void publishValidation(ValidationResult validationResult) {
		File validationFile = new File(directory, validationResult.getTeam() + JSON_EXTENSION);
		ValidationDto dto = new ValidationDto();
		dto.team = validationResult.getTeam();
		dto.points = validationResult.getTotalPoints();

		try {
			ObjectMapper jsonMapper = new ObjectMapper();
			jsonMapper.writeValue(validationFile, dto);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

	@SuppressWarnings("unused")
	private static class ValidationDto {
		public String team;
		public int points;
	}
}
