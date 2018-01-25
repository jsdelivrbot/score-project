package com.dojocoders.score.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.google.common.collect.Maps;

@Document(indexName = "softgames", type = "metrics")
public class Metrics {

	@Id
	private String team;

	private Map<String, String> metrics = Maps.newHashMap();

	@SuppressWarnings("unused")
	private Metrics() {
		// For deserialization
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