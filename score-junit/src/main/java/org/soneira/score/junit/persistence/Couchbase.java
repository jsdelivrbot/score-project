package org.soneira.score.junit.persistence;

import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.transcoder.JacksonTransformers;
import com.google.common.base.Strings;
import org.soneira.score.junit.model.ScoreResult;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Couchbase implements PersistUnit {

	private final static CouchbaseEnvironment ENV = DefaultCouchbaseEnvironment.builder().build();

	private Bucket bucket;

	public Couchbase() {
		String[] nodes = Strings.nullToEmpty(System.getProperty("couchbase.nodes")).split(",");
		String bucketName = Strings.nullToEmpty(System.getProperty("couchbase.bucket"));
		String password = Strings.nullToEmpty(System.getProperty("couchbase.password"));

		Cluster cluster = CouchbaseCluster.create(ENV, nodes);
		this.bucket = cluster.openBucket(bucketName, password);
	}

	public Couchbase(String[] nodes, String bucketName, String password) {
		Cluster cluster = CouchbaseCluster.create(ENV, nodes);
		this.bucket = cluster.openBucket(bucketName, password);
	}

	@Override
	public void putScore(String team, ScoreResult result) {
		try {
			JsonDocument document = JsonDocument.create(team, JsonObject.fromJson(Couchbase.toJSONString(result)));
			bucket.upsert(document);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ScoreResult getScore(String team) {
		JsonDocument result = bucket.get(team);
		if (result == null || result.content() == null || Strings.isNullOrEmpty(result.content().toString())) {
			return new ScoreResult(team);
		}

		try {
			return JacksonTransformers.MAPPER.readValue(result.content().toString(), ScoreResult.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ScoreResult> getAllScores() {
		return bucket
				.query(N1qlQuery.simple("select `scoring` from " + bucket.name()))
				.allRows().stream()
				.map(row -> row.value().get(bucket.name()).toString()).map(Couchbase::fromJSON)
				.collect(Collectors.toList());

	}

	private static ScoreResult fromJSON(String json) {
		try {
			return JacksonTransformers.MAPPER.readValue(json, ScoreResult.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String toJSONString(ScoreResult score) throws JsonProcessingException {
		return JacksonTransformers.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(score);
	}
}
