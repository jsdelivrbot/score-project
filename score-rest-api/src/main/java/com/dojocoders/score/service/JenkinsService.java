package com.dojocoders.score.service;

import com.google.common.base.Throwables;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
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
            LOG.info("status of call {} : {}", post, postResponse.getStatusLine().getStatusCode());

            if(hasFailed(postResponse)) {
                LOG.error("raison of fail : {}", postResponse.getStatusLine().getReasonPhrase());
                LOG.warn("body of fail : {}", EntityUtils.toString(postResponse.getEntity()));
            }
        } catch (IOException e) {
            LOG.error("exception when call " + post + " :", e);
            Throwables.propagate(e);
        }
    }

	/** @see {@link HttpStatus} */
    private boolean hasFailed(HttpResponse requestResponse) {
    	return requestResponse.getStatusLine().getStatusCode() < 200 || requestResponse.getStatusLine().getStatusCode() >= 300;
    }
}
