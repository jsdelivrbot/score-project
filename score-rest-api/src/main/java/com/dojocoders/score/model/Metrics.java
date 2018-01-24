package com.dojocoders.score.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.google.common.collect.Maps;

@Document(indexName = "softgames", type = "metrics")
@org.springframework.data.couchbase.core.mapping.Document
public class Metrics {

	@Id
	@com.couchbase.client.java.repository.annotation.Id
	private String team;

	@com.couchbase.client.java.repository.annotation.Field
	private Map<String, String> metrics = Maps.newHashMap();

	@SuppressWarnings("unused")
	private Metrics() {
		// Used by jackson
	}

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