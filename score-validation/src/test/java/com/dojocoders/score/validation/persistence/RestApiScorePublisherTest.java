package com.dojocoders.score.validation.persistence;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;

import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RestApiScorePublisherTest {

	@Mock
	private HttpClient httpClient;

	@Mock
	private ProtocolVersion protocolVersion;

	@Captor
	private ArgumentCaptor<HttpPost> httpRequest;

	private RestApiScorePublisher restApiScorePublisher;

	@Before
	public void setup() {
		String scorePublisherRestApi = "http://scorePublisherRestApi/" + RestApiScorePublisher.REST_API_URL_TEAM_PARAM + "/" + RestApiScorePublisher.REST_API_URL_POINTS_PARAM;
		restApiScorePublisher = new RestApiScorePublisher(httpClient, scorePublisherRestApi);
	}

	@Test
	public void test_when_call_is_OK() throws IOException, URISyntaxException {
		// Setup
		BasicHttpResponse httpResponse = new BasicHttpResponse(protocolVersion, 200, "");
		httpResponse.setEntity(new StringEntity(""));
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		restApiScorePublisher.putScore("myTeam", 354);

		// Assert
		assertThat(httpRequest.getValue().getMethod()).isEqualTo("POST");
		assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://scorePublisherRestApi/myTeam/354");
	}

	@Test
	public void test_when_call_is_KO() throws IOException {
		// Setup
		BasicHttpResponse httpResponse = new BasicHttpResponse(protocolVersion, 500, "Server Error");
		httpResponse.setEntity(new StringEntity("errorMessageExplained"));
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		try {
			restApiScorePublisher.putScore("myTeam", 354);
			fail(UncheckedIOException.class + " was not thrown");

		} catch (UncheckedIOException e) {
			// Assert
			assertThat(e.getCause()) //
					.isExactlyInstanceOf(HttpResponseException.class) //
					.hasMessageContaining("Server Error") //
					.hasMessageContaining("errorMessageExplained");
			assertThat(((HttpResponseException) e.getCause()).getStatusCode()).isEqualTo(500);
		}
	}
}
