package com.dojocoders.score.infra.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.dojocoders.score.validation.persistence.pojo.CaseResult;
import com.dojocoders.score.validation.persistence.pojo.ErrorCase;
import com.dojocoders.score.validation.persistence.pojo.FailureCase;
import com.dojocoders.score.validation.persistence.pojo.SuccessCase;
import com.dojocoders.score.validation.persistence.pojo.ValidationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class JsonCaseResultTest {

	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private static final Method METHOD_WITH_SUCCESS;
	private static final Method METHOD_WITH_FAILURE;
	private static final Method METHOD_WITH_ERROR;
	static {
		try {
			METHOD_WITH_SUCCESS = JsonCaseResultTest.class.getDeclaredMethod("methodWithSuccess");
			METHOD_WITH_FAILURE = JsonCaseResultTest.class.getDeclaredMethod("methodWithFailure");
			METHOD_WITH_ERROR = JsonCaseResultTest.class.getDeclaredMethod("methodWithError");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void serialize_validationResult() throws Exception {

		AssertionError failure = new AssertionError("assertion failure");
		failure.setStackTrace(new StackTraceElement[] { new StackTraceElement("assertion class", "assertion method", "Assertion.java", 52) });

		NullPointerException error = new NullPointerException("null pointer exception");
		error.setStackTrace(new StackTraceElement[] { new StackTraceElement("NPE class", "NPE method", "NullPointerException.java", 84) });

		List<CaseResult> jsonCaseResults = Lists.newArrayList( //
				new JsonCaseResult(new SuccessCase(METHOD_WITH_SUCCESS, 362)), //
				new JsonCaseResult(new FailureCase(METHOD_WITH_FAILURE, failure)), //
				new JsonCaseResult(new ErrorCase(METHOD_WITH_ERROR, error)));

		ValidationResult result = new ValidationResult("testTeam", 537, jsonCaseResults);

		String serializedResult = JSON_MAPPER.writeValueAsString(result);
		assertThat(new ByteArrayInputStream(serializedResult.getBytes())).hasSameContentAs(this.getClass().getResourceAsStream("serializedResult.json"));
	}

	protected void methodWithSuccess() {
	}

	protected void methodWithFailure() {
	}

	protected void methodWithError() {
	}
}
