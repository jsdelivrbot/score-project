package com.dojocoders.score.service;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JenkinsService {

	public static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

    private static final HttpClient CLIENT = HttpClientBuilder.create().build();

    public void launchJobJenkins(String jenkinsUrl, String jenkinsJobName, String jenkinsJobToken) {
        HttpPost post = new HttpPost(jenkinsUrl + "/buildByToken/build?job=" + jenkinsJobName + "&token=" + jenkinsJobToken);
        try {
            HttpResponse postResponse = CLIENT.execute(post);
            LOG.info("Status of call {} : {}", post, postResponse.getStatusLine().getStatusCode());

            if(hasFailed(postResponse)) {
                throw new HttpResponseException(postResponse.getStatusLine().getStatusCode(), postResponse.getStatusLine().getReasonPhrase() +
                		", code " + postResponse.getStatusLine().getStatusCode() + ", body of response :\n" + EntityUtils.toString(postResponse.getEntity()));
            }
        } catch (IOException e) {
        	LOG.error("Exception when call " + post, e);
        }
    }

	/** @see {@link HttpStatus} */
    private boolean hasFailed(HttpResponse requestResponse) {
    	return requestResponse.getStatusLine().getStatusCode() < 200 || requestResponse.getStatusLine().getStatusCode() >= 300;
    }
}
