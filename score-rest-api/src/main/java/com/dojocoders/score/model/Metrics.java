package com.dojocoders.score.model;

import java.util.Map;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import com.google.common.collect.Maps;

@Document
public class Metrics {

	@Id
	private String team;

	@Field
	private Map<String, String> metrics = Maps.newHashMap();

	public Metrics(String team) {
		this.team = team;
	}

	public String getTeam() {
		return team;
	}

	public Map<String, String> getMetrics() {
		return this.metrics;
	}
}