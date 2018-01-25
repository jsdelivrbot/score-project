package com.dojocoders.score.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "softgames", type = "config")
public class Configuration {

	@Id
	private String mode;

	private Integer sprintTime;

	private String jenkinsUrl;

	private String jenkinsJobName;

	private String jenkinsJobToken;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Integer getSprintTime() {
		return sprintTime;
	}

	public void setSprintTime(Integer sprintTime) {
		this.sprintTime = sprintTime;
	}

	public String getJenkinsUrl() {
		return jenkinsUrl;
	}

	public void setJenkinsUrl(String jenkinsUrl) {
		this.jenkinsUrl = jenkinsUrl;
	}

	public String getJenkinsJobName() {
		return jenkinsJobName;
	}

	public void setJenkinsJobName(String jenkinsJobName) {
		this.jenkinsJobName = jenkinsJobName;
	}

	public String getJenkinsJobToken() {
		return jenkinsJobToken;
	}

	public void setJenkinsJobToken(String jenkinsJobToken) {
		this.jenkinsJobToken = jenkinsJobToken;
	}
}
