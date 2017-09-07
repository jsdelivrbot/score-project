package com.dojocoders.score.validation.persistence;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.impl.client.CloseableHttpClient;

import com.dojocoders.score.infra.RestApiClient;
import com.dojocoders.score.infra.validation.JsonCaseResult;
import com.dojocoders.score.validation.persistence.pojo.CaseResult;
import com.dojocoders.score.validation.persistence.pojo.ValidationResult;

public class RestApiValidationPublisher implements ValidationPublisher, AutoCloseable {

	public static final String REST_API_URL_TEAM_PARAM = "${team}";
	public static final String REST_API_URL_POINTS_PARAM = "${points}";

	private RestApiClient restApiClient;

	public RestApiValidationPublisher(String validationPublisherApiUrl) {
		this.restApiClient = new RestApiClient(validationPublisherApiUrl, RestApiClient.STRICT_SUCCESS_HTTP_CODE);
	}

	public RestApiValidationPublisher(String validationPublisherApiUrl, CloseableHttpClient client) {
		this.restApiClient = new RestApiClient(validationPublisherApiUrl, RestApiClient.STRICT_SUCCESS_HTTP_CODE, client);
	}

	@Override
	public void publishValidation(ValidationResult validationResult) {
		List<CaseResult> jsonCaseResults = validationResult.getCaseResults().stream().map(JsonCaseResult::new).collect(Collectors.toList());
		ValidationResult jsonValidationResult = new ValidationResult(validationResult.getTeam(), validationResult.getTotalPoints(), jsonCaseResults);

		restApiClient.post(jsonValidationResult);
	}

	@Override
	public void close() throws IOException {
		restApiClient.close();
	}
}
