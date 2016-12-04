package com.dojocoders.score.junit.persistence;

import com.dojocoders.score.junit.TestConfiguration;
import com.google.common.base.Throwables;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ScoreApiRest implements TestPersistUnit {

    private static final HttpClient CLIENT = HttpClientBuilder.create().build();

	@Override
	public void putScore(String team, Long points) {
		String scoreIhmUrl = TestConfiguration.getRestApiUrl();
		HttpPost post = new HttpPost(scoreIhmUrl + "/api/scores/add/" + team + "/" + String.valueOf(points));
        try {
            System.out.println("Calling : " + post.toString());
            HttpResponse postResponse = CLIENT.execute(post);

            if(postResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new HttpResponseException(postResponse.getStatusLine().getStatusCode(), postResponse.getStatusLine().getReasonPhrase() +
                		", code " + postResponse.getStatusLine().getStatusCode() + ", body of response :\n" + EntityUtils.toString(postResponse.getEntity()));
            }
        } catch (IOException e) {
        	System.out.println("Exception when call " + post);
        	Throwables.propagate(e);
        }
    }

	public HttpClient createHttpClient() {
		return HttpClientBuilder.create().build();
	}

}
