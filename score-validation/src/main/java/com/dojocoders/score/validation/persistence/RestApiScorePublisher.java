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

	private static final HttpClient DEFAULT_HTTP_CLIENT = HttpClientBuilder.create().build();

	public RestApiScorePublisherConfiguration restApiPersistenceConfiguration;

	private HttpClient client;

	public RestApiScorePublisher() {
		this(DEFAULT_HTTP_CLIENT, new RestApiScorePublisherConfiguration());
	}

	public RestApiScorePublisher(HttpClient client, RestApiScorePublisherConfiguration restApiPersistenceConfiguration) {
		this.client = client;
		this.restApiPersistenceConfiguration = restApiPersistenceConfiguration;
	}

	@Override
	public void putScore(String team, int points) {
		HttpPost post = new HttpPost(restApiPersistenceConfiguration.computeRestApiUrl(team, points));
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
}
