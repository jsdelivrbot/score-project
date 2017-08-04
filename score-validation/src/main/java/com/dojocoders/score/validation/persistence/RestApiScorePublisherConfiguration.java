package com.dojocoders.score.validation.persistence;

import com.dojocoders.codingwars.validation.PersistenceConfiguration;

public class RestApiScorePublisherConfiguration {

	public static final String REST_API_URL_PARAMETER = "score.rest-api.url";
	public static final String REST_API_URL_TEAM_PARAM = "${team}";
	public static final String REST_API_URL_POINTS_PARAM = "${points}";
	public static final String DEFAULT_REST_API_URL = "http://localhost:8080/api/scores/add/" + REST_API_URL_TEAM_PARAM + "/" + REST_API_URL_POINTS_PARAM;

	public String getRestApiUrl() {
		return PersistenceConfiguration.getProperty(REST_API_URL_PARAMETER, DEFAULT_REST_API_URL);
	}

	public String computeRestApiUrl(String team, int points) {
		return getRestApiUrl() //
				.replaceAll(REST_API_URL_TEAM_PARAM, team) //
				.replaceAll(REST_API_URL_POINTS_PARAM, String.valueOf(points));
	}
}
