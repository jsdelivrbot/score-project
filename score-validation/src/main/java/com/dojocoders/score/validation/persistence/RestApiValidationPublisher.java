package com.dojocoders.score.validation.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.dojocoders.score.validation.persistence.pojo.ValidationResult;

public class RestApiValidationPublisher implements ValidationPublisher {

	public static final String REST_API_URL_TEAM_PARAM = "${team}";
	public static final String REST_API_URL_POINTS_PARAM = "${points}";

	private static final HttpClient DEFAULT_HTTP_CLIENT = HttpClientBuilder.create().build();

	public String validationPublisherApiUrl;

	private HttpClient client;

	public RestApiValidationPublisher(String validationPublisherApiUrl) {
		this(DEFAULT_HTTP_CLIENT, validationPublisherApiUrl);
	}

	public RestApiValidationPublisher(HttpClient client, String validationPublisherApiUrl) {
		this.client = client;
		this.validationPublisherApiUrl = validationPublisherApiUrl;
	}

	@Override
	public void publishValidation(ValidationResult validationResult) {
		HttpPost post = new HttpPost(computeRestApiUrl(validationResult.getTeam(), validationResult.getTotalPoints()));
		try {
			HttpResponse postResponse = client.execute(post);

			if (postResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new HttpResponseException(postResponse.getStatusLine().getStatusCode(), postResponse.getStatusLine().getReasonPhrase() + ", code "
						+ postResponse.getStatusLine().getStatusCode() + ", body of response :\n" + EntityUtils.toString(postResponse.getEntity()));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String computeRestApiUrl(String team, int points) {
		return validationPublisherApiUrl //
				.replace(REST_API_URL_TEAM_PARAM, team) //
				.replace(REST_API_URL_POINTS_PARAM, String.valueOf(points));
	}
}
