package com.dojocoders.score.validation.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;

import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.dojocoders.score.infra.RestApiClientTest.BasicCloseableHttpResponse;
import com.dojocoders.score.validation.persistence.pojo.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class RestApiValidationPublisherTest {

	@Mock
	private CloseableHttpClient httpClient;

	@Mock
	private ProtocolVersion protocolVersion;

	@Captor
	private ArgumentCaptor<HttpPost> httpRequest;

	private RestApiValidationPublisher restApiValidationPublisher;

	@Before
	public void setup() {
		String validationPublisherRestApi = "http://validationPublisherRestApi/";
		restApiValidationPublisher = new RestApiValidationPublisher(validationPublisherRestApi, httpClient);
	}

	@Test
	public void test_publishValidation_when_call_is_OK() throws IOException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 200, "");
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);
		ValidationResult validationResult = new ValidationResult("myTeam", 354, new ArrayList<>());

		// Test
		restApiValidationPublisher.publishValidation(validationResult);

		// Assert
		assertThat(httpRequest.getValue().getMethod()).isEqualTo("POST");
		assertThat(httpRequest.getValue().getURI().toASCIIString()).isEqualTo("http://validationPublisherRestApi");
		assertThat(EntityUtils.toString(httpRequest.getValue().getEntity()))
				.isEqualTo("{\"team\":\"myTeam\",\"totalPoints\":354,\"caseResults\":[]}");
	}

	@Test
	public void test_publishValidation_when_call_is_KO() throws IOException {
		// Setup
		BasicCloseableHttpResponse httpResponse = new BasicCloseableHttpResponse(protocolVersion, 201, "Not Strict HTTP OK");
		httpResponse.setEntity(new StringEntity("errorMessageExplained"));
		when(httpClient.execute(httpRequest.capture())).thenReturn(httpResponse);
		ValidationResult validationResult = new ValidationResult("myTeam", 354, new ArrayList<>());

		// Test
		try {
			restApiValidationPublisher.publishValidation(validationResult);
			fail(UncheckedIOException.class + " was not thrown");

		} catch (UncheckedIOException e) {
			// Assert
			assertThat(e.getCause()) //
					.isExactlyInstanceOf(HttpResponseException.class) //
					.hasMessageContaining("Not Strict HTTP OK") //
					.hasMessageContaining("errorMessageExplained");
			assertThat(((HttpResponseException) e.getCause()).getStatusCode()).isEqualTo(201);
		}
	}

	@Test
	public void test_close() throws IOException {
		// Test
		restApiValidationPublisher.close();

		// Assert
		Mockito.verify(httpClient).close();
	}
}
