package com.dojocoders.score.validation.bridge;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RestApiBridge {

	private static final HttpClient DEFAULT_HTTP_CLIENT = HttpClientBuilder.create().build();

	public RestApiBridgeConfiguration restApiBridgeConfiguration;

	private HttpClient client;

	public RestApiBridge() {
		this(DEFAULT_HTTP_CLIENT, new RestApiBridgeConfiguration());
	}

	public RestApiBridge(HttpClient client, RestApiBridgeConfiguration restApiBridgeConfiguration) {
		this.client = client;
		this.restApiBridgeConfiguration = restApiBridgeConfiguration;
	}

	public <Request, Response> Response execute(Request request, Class<Response> responseClass) {
		try {
			ObjectMapper jsonMapper = new ObjectMapper();
			String jsonInString = jsonMapper.writeValueAsString(request);

			HttpPost post = new HttpPost(restApiBridgeConfiguration.getRestApiUrl());
			post.setEntity(new StringEntity(jsonInString));

			HttpResponse postResponse = client.execute(post);

			if (postResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new HttpResponseException(postResponse.getStatusLine().getStatusCode(), postResponse.getStatusLine().getReasonPhrase() + ", code "
						+ postResponse.getStatusLine().getStatusCode() + ", body of response :\n" + EntityUtils.toString(postResponse.getEntity()));
			}

			return jsonMapper.readValue(postResponse.getEntity().getContent(), responseClass);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
