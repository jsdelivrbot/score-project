package com.dojocoders.score.infra;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

public class RestApiClient implements AutoCloseable {

	public static final RangeSet<Integer> HTTP_INFORMATION_CODES = ImmutableRangeSet.of(Range.closedOpen(100, 200));
	public static final RangeSet<Integer> HTTP_SUCCESS_CODES = ImmutableRangeSet.of(Range.closedOpen(200, 300));
	public static final RangeSet<Integer> HTTP_REDIRECT_CODES = ImmutableRangeSet.of(Range.closedOpen(300, 400));
	public static final RangeSet<Integer> HTTP_CLIENT_ERROR_CODES = ImmutableRangeSet.of(Range.closedOpen(400, 500));
	public static final RangeSet<Integer> HTTP_SERVER_ERROR_CODES = ImmutableRangeSet.of(Range.closedOpen(500, 600));

	public static final RangeSet<Integer> STRICT_SUCCESS_HTTP_CODE = ImmutableRangeSet.of(Range.singleton(200));

	public static final RangeSet<Integer> WITHOUT_ERROR_HTTP_CODES = ImmutableRangeSet.<Integer>builder() //
			.addAll(HTTP_INFORMATION_CODES).addAll(HTTP_SUCCESS_CODES).addAll(HTTP_REDIRECT_CODES) //
			.build();

	public static final RangeSet<Integer> DEFAULT_VALID_RESPONSE_HTTP_CODES = STRICT_SUCCESS_HTTP_CODE;

	private final ObjectMapper jsonMapper = new ObjectMapper();
	private final String restApiBaseUrl;
	private final CloseableHttpClient client;
	private final RangeSet<Integer> defaultValidResponseHttpCodes;

	private final static String NOT_DEFINED_REQUEST_PATH = new String();
	private final static RangeSet<Integer> NOT_DEFINED_VALID_RESPONSE_HTTP_CODES = ImmutableRangeSet.of();

	private String nextRequestPath = NOT_DEFINED_REQUEST_PATH;
	private RangeSet<Integer> nextRequestValidResponseHttpCodes = NOT_DEFINED_VALID_RESPONSE_HTTP_CODES;

	private String currentRequestPath;
	private RangeSet<Integer> currentRequestValidResponseHttpCodes;

	public RestApiClient(String restApiBaseUrl) {
		this(restApiBaseUrl, DEFAULT_VALID_RESPONSE_HTTP_CODES, HttpClients.createDefault());
	}

	public RestApiClient(String restApiBaseUrl, int maxParallelRequests) {
		this(restApiBaseUrl, DEFAULT_VALID_RESPONSE_HTTP_CODES, createHttpClientWithDefaultMaxPerRoute(maxParallelRequests));
	}

	public RestApiClient(String restApiBaseUrl, CloseableHttpClient client) {
		this(restApiBaseUrl, DEFAULT_VALID_RESPONSE_HTTP_CODES, client);
	}

	public RestApiClient(String restApiBaseUrl, RangeSet<Integer> defaultValidResponseHttpCodes) {
		this(restApiBaseUrl, defaultValidResponseHttpCodes, HttpClients.createDefault());
	}

	public RestApiClient(String restApiBaseUrl, RangeSet<Integer> defaultValidResponseHttpCodes, int maxParallelRequests) {
		this(restApiBaseUrl, defaultValidResponseHttpCodes, createHttpClientWithDefaultMaxPerRoute(maxParallelRequests));
	}

	public RestApiClient(String restApiBaseUrl, RangeSet<Integer> defaultValidResponseHttpCodes, CloseableHttpClient client) {
		this.restApiBaseUrl = Strings.nullToEmpty(restApiBaseUrl).endsWith("/") ? restApiBaseUrl.substring(0, restApiBaseUrl.length() - 1) : restApiBaseUrl;
		this.defaultValidResponseHttpCodes = defaultValidResponseHttpCodes;
		this.client = client;
	}

	private static CloseableHttpClient createHttpClientWithDefaultMaxPerRoute(int maxParallelRequests) {
		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
		poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxParallelRequests);
		return HttpClients.createMinimal(poolingHttpClientConnectionManager);
	}

	/** Define the sub-path for the next executed request */
	public RestApiClient withPath(String path) {
		nextRequestPath = Strings.nullToEmpty(path).startsWith("/") ? path.substring(1, path.length()) : path;
		return this;
	}

	/** Define the set of valid http code for the next executed request */
	public RestApiClient withValidResponseHttpCodes(RangeSet<Integer> validResponseHttpCodes) {
		nextRequestValidResponseHttpCodes = validResponseHttpCodes;
		return this;
	}

	/**
	 * Move next execution parameters into currents to correctly manage any
	 * exception throwing
	 */
	private void startRequestExecution() {
		currentRequestPath = nextRequestPath;
		currentRequestValidResponseHttpCodes = nextRequestValidResponseHttpCodes;
		nextRequestPath = NOT_DEFINED_REQUEST_PATH;
		nextRequestValidResponseHttpCodes = NOT_DEFINED_VALID_RESPONSE_HTTP_CODES;
	}

	private String getRequestPath() {
		// Use reference equality to ensure that parameter has been defined
		if (currentRequestPath == NOT_DEFINED_REQUEST_PATH) {
			return restApiBaseUrl;
		} else {
			return restApiBaseUrl + "/" + currentRequestPath;
		}
	}

	private RangeSet<Integer> getValidResponseHttpCodes() {
		// Use reference equality to ensure that parameter has been defined
		return (currentRequestValidResponseHttpCodes == NOT_DEFINED_VALID_RESPONSE_HTTP_CODES) ? defaultValidResponseHttpCodes : currentRequestValidResponseHttpCodes;
	}

	public void post() {
		startRequestExecution();
		HttpPost httpRequest = new HttpPost(getRequestPath());
		execute(httpRequest);
	}

	public <Request> void post(Request request) {
		startRequestExecution();
		HttpPost httpRequest = new HttpPost(getRequestPath());
		executeWithBody(httpRequest, request);
	}

	public <Response> Response post(Class<Response> responseType) {
		startRequestExecution();
		HttpPost httpRequest = new HttpPost(getRequestPath());
		return executeWithoutBody(httpRequest, responseType);
	}

	public <Request, Response> Response post(Request request, Class<Response> responseType) {
		startRequestExecution();
		HttpPost httpRequest = new HttpPost(getRequestPath());
		return execute(httpRequest, request, responseType);
	}

	public void get() {
		startRequestExecution();
		HttpGet httpRequest = new HttpGet(getRequestPath());
		execute(httpRequest);
	}

	public <Response> Response get(Class<Response> responseType) {
		startRequestExecution();
		HttpGet httpRequest = new HttpGet(getRequestPath());
		return executeWithoutBody(httpRequest, responseType);
	}

	private void execute(HttpRequestBase requestWithoutBody) {
		internalExecute(requestWithoutBody, Optional.empty());
	}

	private <Request> void executeWithBody(HttpEntityEnclosingRequestBase requestWithBody, Request request) {
		internalExecute(requestWithBody, request, Optional.empty());
	}

	private <Response> Response executeWithoutBody(HttpRequestBase requestWithoutBody, Class<Response> responseType) {
		return internalExecute(requestWithoutBody, Optional.of(responseType));
	}

	private <Request, Response> Response execute(HttpEntityEnclosingRequestBase requestWithBody, Request request, Class<Response> responseType) {
		return internalExecute(requestWithBody, request, Optional.of(responseType));
	}

	private <Request, Response> Response internalExecute(HttpEntityEnclosingRequestBase requestWithBody, Request request, Optional<Class<Response>> responseType) {
		try {
			String requestAsJsonString = jsonMapper.writeValueAsString(request);
			requestWithBody.setEntity(new StringEntity(requestAsJsonString, ContentType.APPLICATION_JSON));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		return internalExecute(requestWithBody, responseType);
	}

	private <Request, Response> Response internalExecute(HttpRequestBase requestWithoutBody, Optional<Class<Response>> responseType) {
		try (CloseableHttpResponse postResponse = client.execute(requestWithoutBody)) {

			if (hasFailed(postResponse)) {
				throw new HttpResponseException(postResponse.getStatusLine().getStatusCode(), postResponse.getStatusLine().getReasonPhrase() + ", code "
						+ postResponse.getStatusLine().getStatusCode() + ", body of response :\n" + EntityUtils.toString(postResponse.getEntity()));
			}

			Response responseValue = null;
			if (responseType.isPresent()) {
				responseValue = jsonMapper.readValue(postResponse.getEntity().getContent(), responseType.get());
			}
			return responseValue;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private boolean hasFailed(HttpResponse requestResponse) {
		return !getValidResponseHttpCodes().contains(requestResponse.getStatusLine().getStatusCode());
	}

	public void close() throws IOException {
		client.close();
	}
}
