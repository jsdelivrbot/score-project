package com.dojocoders.score.validation.persistence;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileScorePublisher implements ScorePublisher {
	public static final String JSON_EXTENSION = ".json";

	private File directory;

	public JsonFileScorePublisher(File directory) {
		this.directory = directory;
	}

	@Override
	public void putScore(String team, int points) {
		File scoreFile = new File(directory, team + JSON_EXTENSION);
		ScoreDTO dto = new ScoreDTO();
		dto.team = team;
		dto.points = points;

		try {
			ObjectMapper jsonMapper = new ObjectMapper();
			jsonMapper.writeValue(scoreFile, dto);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

	@SuppressWarnings("unused")
	private static class ScoreDTO {
		public String team;
		public int points;
	}
}
