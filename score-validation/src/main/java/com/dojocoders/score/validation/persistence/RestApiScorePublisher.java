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

public class RestApiScorePublisher implements ScorePublisher {

	public static final String REST_API_URL_TEAM_PARAM = "${team}";
	public static final String REST_API_URL_POINTS_PARAM = "${points}";

	private static final HttpClient DEFAULT_HTTP_CLIENT = HttpClientBuilder.create().build();

	public String scorePublisherApiUrl;

	private HttpClient client;

	public RestApiScorePublisher(String scorePublisherApiUrl) {
		this(DEFAULT_HTTP_CLIENT, scorePublisherApiUrl);
	}

	public RestApiScorePublisher(HttpClient client, String scorePublisherApiUrl) {
		this.client = client;
		this.scorePublisherApiUrl = scorePublisherApiUrl;
	}

	@Override
	public void putScore(String team, int points) {
		HttpPost post = new HttpPost(computeRestApiUrl(team, points));
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
		return scorePublisherApiUrl //
				.replace(REST_API_URL_TEAM_PARAM, team) //
				.replace(REST_API_URL_POINTS_PARAM, String.valueOf(points));
	}
}
