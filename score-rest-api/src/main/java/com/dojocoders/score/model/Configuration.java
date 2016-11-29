package com.dojocoders.score.model;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class Configuration {

    @Id
    private String mode;

    @Field
    private Integer sprintTime;

    @Field
    private String jenkinsUrl;

    @Field
    private String jenkinsJobName;

    @Field
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
