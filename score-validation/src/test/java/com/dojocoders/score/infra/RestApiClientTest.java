package com.dojocoders.score.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;

import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;

@RunWith(MockitoJUnitRunner.class)
public class RestApiClientTest {

	private enum RequestType {
		POST, POST_WITH_BODY, POST_WITH_RESPONSE, POST_WITH_BODY_AND_RESPONSE, //
		GET, GET_WITH_RESPONSE,
	}

	@Mock
	private CloseableHttpClient httpClient;

	@Mock
	private ProtocolVersion protocolVersion;

	@Captor
	private ArgumentCaptor<HttpRequestBase> httpRequest;

	private RestApiClient restApiClient;

	@Before
	public void setup() {
		restApiClient = new RestApiClient("http://remoteRestApi/", RestApiClient.WITHOUT_ERROR_HTTP_CODES, httpClient);
	}

	@Test
	public void test_post_with_status_code_OK() throws IOException, URISyntaxException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 200, "");
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		restApiClient.post();

		// Assert
		assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi");
		assertThat(httpRequest.getValue().getMethod()).isEqualTo("POST");
		assertThat(httpRequest.getValue()).isExactlyInstanceOf(HttpPost.class);
		assertThat(((HttpPost) httpRequest.getValue()).getEntity()).isNull();
		assertThat(httpResponse.closed).isTrue();
	}

	@Test
	public void test_post_with_request_and_status_code_OK() throws IOException, URISyntaxException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 200, "");
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		restApiClient.post(false);

		// Assert
		assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi");
		assertThat(httpRequest.getValue().getMethod()).isEqualTo("POST");
		assertThat(httpRequest.getValue()).isExactlyInstanceOf(HttpPost.class);
		assertThat(EntityUtils.toString(((HttpPost) httpRequest.getValue()).getEntity())).isEqualTo("false");
		assertThat(httpResponse.closed).isTrue();
	}

	@Test
	public void test_post_with_response_and_status_code_OK() throws IOException, URISyntaxException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 200, "");
		httpResponse.setEntity(new StringEntity("true"));
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		Boolean response = restApiClient.post(Boolean.class);

		// Assert
		assertThat(response).isTrue();
		assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi");
		assertThat(httpRequest.getValue().getMethod()).isEqualTo("POST");
		assertThat(httpRequest.getValue()).isExactlyInstanceOf(HttpPost.class);
		assertThat(((HttpPost) httpRequest.getValue()).getEntity()).isNull();
		assertThat(httpResponse.closed).isTrue();
	}

	@Test
	public void test_post_with_request_and_response_and_status_code_OK() throws IOException, URISyntaxException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 200, "");
		httpResponse.setEntity(new StringEntity("true"));
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		Boolean response = restApiClient.post(false, Boolean.class);

		// Assert
		assertThat(response).isTrue();
		assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi");
		assertThat(httpRequest.getValue().getMethod()).isEqualTo("POST");
		assertThat(httpRequest.getValue()).isExactlyInstanceOf(HttpPost.class);
		assertThat(EntityUtils.toString(((HttpPost) httpRequest.getValue()).getEntity())).isEqualTo("false");
		assertThat(httpResponse.closed).isTrue();
	}

	@Test
	public void test_get_with_status_code_OK() throws IOException, URISyntaxException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 200, "");
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		restApiClient.get();

		// Assert
		assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi");
		assertThat(httpRequest.getValue().getMethod()).isEqualTo("GET");
		assertThat(httpRequest.getValue()).isExactlyInstanceOf(HttpGet.class);
		assertThat(httpResponse.closed).isTrue();
	}

	@Test
	public void test_get_with_response_and_status_code_OK() throws IOException, URISyntaxException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 200, "");
		httpResponse.setEntity(new StringEntity("true"));
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		Boolean response = restApiClient.get(Boolean.class);

		// Assert
		assertThat(response).isTrue();
		assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi");
		assertThat(httpRequest.getValue().getMethod()).isEqualTo("GET");
		assertThat(httpRequest.getValue()).isExactlyInstanceOf(HttpGet.class);
		assertThat(httpResponse.closed).isTrue();
	}

	@Test
	public void test_use_and_reset_of_path_and_specific_valid_codes_with_request_OK() throws IOException, URISyntaxException {
		for (RequestType requestType : RequestType.values()) {
			// Setup
			BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 527, "Specific Error");
			httpResponse.setEntity(new StringEntity("\"errorMessageExplained\""));
			when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

			// Test
			restApiClient.withPath("specific").withValidResponseHttpCodes(ImmutableRangeSet.of(Range.singleton(527)));
			execute(requestType);

			// Assert
			assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi/specific");
			try {
				execute(requestType);
				failBecauseExceptionWasNotThrown(UncheckedIOException.class);
			} catch (UncheckedIOException e) {
				assertThat(e.getCause()) //
						.isExactlyInstanceOf(HttpResponseException.class) //
						.hasMessageContaining("Specific Error") //
						.hasMessageContaining("errorMessageExplained");
				assertThat(((HttpResponseException) e.getCause()).getStatusCode()).isEqualTo(527);
			}
			assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi");
		}
	}

	@Test
	public void test_use_and_reset_of_path_and_specific_valid_codes_with_request_KO() throws IOException, URISyntaxException {
		for (RequestType requestType : RequestType.values()) {
			// Setup
			BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 301, "Redirect");
			httpResponse.setEntity(new StringEntity("\"redirect to http://blabla ...\""));
			when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

			// Test
			restApiClient.withPath("/specific").withValidResponseHttpCodes(ImmutableRangeSet.of(Range.singleton(200)));
			try {
				execute(requestType);
				failBecauseExceptionWasNotThrown(UncheckedIOException.class);
			} catch (UncheckedIOException e) {
				assertThat(e.getCause()) //
						.isExactlyInstanceOf(HttpResponseException.class) //
						.hasMessageContaining("Redirect") //
						.hasMessageContaining("redirect to http://blabla ...");
				assertThat(((HttpResponseException) e.getCause()).getStatusCode()).isEqualTo(301);
			}

			// Assert
			assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi/specific");
			execute(requestType);
			assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://remoteRestApi");
		}
	}

	@Test
	public void test_post_with_request_with_json_serialization_KO() throws IOException, URISyntaxException {
		// Test
		try {
			BasicCloseableHttpResponse impossibleToSerialize = new BasicCloseableHttpResponse(protocolVersion, 200, "");
			restApiClient.post(impossibleToSerialize);
			failBecauseExceptionWasNotThrown(UncheckedIOException.class);
		} catch (UncheckedIOException e) {

			// Assert
			assertThat(e.getCause()).isExactlyInstanceOf(JsonMappingException.class);
		}
	}

	@Test
	public void test_get_with_response_with_json_deserialization_KO() throws IOException, URISyntaxException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 200, "");
		httpResponse.setEntity(new StringEntity("toto"));
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);

		// Test
		try {
			restApiClient.get(Boolean.class);
			failBecauseExceptionWasNotThrown(UncheckedIOException.class);
		} catch (UncheckedIOException e) {

			// Assert
			assertThat(e.getCause()) //
					.isExactlyInstanceOf(JsonParseException.class) //
					.hasMessageContaining("toto");
		}
	}

	private void execute(RequestType requestType) {
		switch (requestType) {
		case POST:
			restApiClient.post();
			break;
		case POST_WITH_BODY:
			restApiClient.post(false);
			break;
		case POST_WITH_RESPONSE:
			restApiClient.post(String.class);
			break;
		case POST_WITH_BODY_AND_RESPONSE:
			restApiClient.post(false, String.class);
			break;
		case GET:
			restApiClient.get();
			break;
		case GET_WITH_RESPONSE:
			restApiClient.get(String.class);
			break;
		default:
			throw new UnsupportedOperationException("Unknown RequestType " + requestType);
		}
	}

	private static class BasicCloseableHttpResponse extends BasicHttpResponse implements CloseableHttpResponse {
		public boolean closed = false;

		public BasicCloseableHttpResponse(ProtocolVersion version, int code, String reason) {
			super(version, code, reason);
		}

		@Override
		public void close() throws IOException {
			closed = true;
		}

	}
}
