package com.dojocoders.score.infra.validation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import com.dojocoders.score.validation.persistence.pojo.CaseResult;
import com.dojocoders.score.validation.persistence.pojo.ErrorCase;
import com.dojocoders.score.validation.persistence.pojo.FailureCase;
import com.dojocoders.score.validation.persistence.pojo.SuccessCase;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "testName", "resultType", "points", "class", "message", "stackTrace" })
public final class JsonCaseResult extends CaseResult {

	private final CaseResultType resultType;

	private final Map<String, Object> properties = new LinkedHashMap<>();

	public JsonCaseResult(CaseResult caseResult) {
		super(caseResult.getDescription());
		resultType = caseResult.getType();
	}

	public JsonCaseResult(SuccessCase sucessCase) {
		this((CaseResult) sucessCase);
		properties.put("points", sucessCase.getPoints());
	}

	public JsonCaseResult(FailureCase failureCase) {
		this((CaseResult) failureCase);
		addExceptionProperties(failureCase.getFailure());
	}

	public JsonCaseResult(ErrorCase errorCase) {
		this((CaseResult) errorCase);
		addExceptionProperties(errorCase.getError());
	}

	private void addExceptionProperties(Throwable exception) {
		properties.put("class", exception.getClass().getName());
		properties.put("message", exception.getMessage());
		StringWriter strackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(strackTrace));
		// String normalizedStrackTrace = strackTrace.toString() //
		// .replace("\r", "").replace("\n", "").replace("\t", " - ");
		properties.put("stackTrace", strackTrace.toString());// normalizedStrackTrace);
	}

	@JsonIgnore
	public Method getDescription() {
		return super.getDescription();
	}

	public String getTestName() {
		return getDescription().getDeclaringClass().getName() + "." + getDescription().getName();
	}

	@JsonProperty("resultType")
	public CaseResultType getType() {
		return resultType;
	}

	@JsonAnyGetter
	private Map<String, Object> getProperties() {
		return properties;
	}
}