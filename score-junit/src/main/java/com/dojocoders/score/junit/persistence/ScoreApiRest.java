package com.dojocoders.score.junit.persistence;

import com.dojocoders.score.junit.TestConfiguration;
import com.google.common.base.Throwables;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class ScoreApiRest implements TestPersistUnit {

	@Override
	public void putScore(String team, Integer points) {
		String scoreIhmUrl = TestConfiguration.getRestApiUrl();
		HttpPost post = new HttpPost(scoreIhmUrl + "/api/score/" + team + "/" + String.valueOf(points));
        try {
            System.out.println("Calling  : " + post.toString());
            createHttpClient().execute(post);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

	public HttpClient createHttpClient() {
		return HttpClientBuilder.create().build();
	}

}
