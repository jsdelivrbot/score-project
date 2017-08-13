package com.dojocoders.score.validation.bridge;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RestApiBridge implements Closeable {

	private String remoteApiUrl;

	private CloseableHttpClient client;

	private ObjectMapper jsonMapper = new ObjectMapper();

	public RestApiBridge(String remoteApiUrl) {
		this(remoteApiUrl, HttpClients.createDefault());
	}

	public RestApiBridge(String remoteApiUrl, int maxParallelRequests) {
		this.remoteApiUrl = remoteApiUrl;
		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
		poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxParallelRequests);
		this.client = HttpClients.createMinimal(poolingHttpClientConnectionManager);
	}

	public RestApiBridge(String remoteApiUrl, CloseableHttpClient client) {
		this.remoteApiUrl = remoteApiUrl;
		this.client = client;
	}

	public <Request, Response> Response execute(Request request, Class<Response> responseClass) {
		try {
			String jsonInString = jsonMapper.writeValueAsString(request);

			HttpPost post = new HttpPost(remoteApiUrl);
			post.setEntity(new StringEntity(jsonInString, ContentType.APPLICATION_JSON));

			try (CloseableHttpResponse postResponse = client.execute(post)) {

				if (postResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					throw new HttpResponseException(postResponse.getStatusLine().getStatusCode(), postResponse.getStatusLine().getReasonPhrase() + ", code "
							+ postResponse.getStatusLine().getStatusCode() + ", body of response :\n" + EntityUtils.toString(postResponse.getEntity()));
				}

				return jsonMapper.readValue(postResponse.getEntity().getContent(), responseClass);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void close() throws IOException {
		client.close();
	}
}
