package com.dojocoders.score.service;

import com.google.common.base.Throwables;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JenkinsService {

    public void launchJobJenkins(String jenkinsUrl, String jenkinsJobName, String jenkinsJobToken) {
        HttpPost post = new HttpPost(jenkinsUrl + "/job/" + jenkinsJobName + "/build?token=" + jenkinsJobToken);
        HttpClient client = HttpClientBuilder.create().build();
        try {
            client.execute(post);
        } catch (IOException e) {
            Throwables.propagate(e);
        }

        return;
    }
}
