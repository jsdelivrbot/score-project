package com.dojocoders.score.infra;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;

import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RestApiClientTest {

	@Mock
	private CloseableHttpClient httpClient;

	@Mock
	private ProtocolVersion protocolVersion;

	@Captor
	private ArgumentCaptor<HttpPost> httpRequest;

	private RestApiClient restApiClient;

	@Before
	public void setup() {
		restApiClient = new RestApiClient("http://remoteRestApi/", RestApiClient.WITHOUT_ERROR_HTTP_CODES, httpClient);
	}

	@Test
	public void test_when_call_is_OK() throws IOException, URISyntaxException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 200, "");
		httpResponse.setEntity(new StringEntity("true"));
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		Boolean response = restApiClient.post(false, Boolean.class);

		// Assert
		assertThat(response).isTrue();
		assertThat(EntityUtils.toString(httpRequest.getValue().getEntity())).isEqualTo("false");
		assertThat(httpRequest.getValue().getMethod()).isEqualTo("POST");
		assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi/");
	}

	@Test
	public void test_when_call_is_KO() throws IOException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 500, "Server Error");
		httpResponse.setEntity(new StringEntity("errorMessageExplained"));
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		try {
			restApiClient.post(false, Boolean.class);
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

	public static class BasicCloseableHttpResponse extends BasicHttpResponse implements CloseableHttpResponse {

		public BasicCloseableHttpResponse(ProtocolVersion version, int code, String reason) {
			super(version, code, reason);
		}

		@Override
		public void close() throws IOException {
		}

	}
}
