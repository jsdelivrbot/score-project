package com.dojocoders.score.model;

import com.google.common.collect.Lists;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "softgames", type = "sprint")
@org.springframework.data.couchbase.core.mapping.Document
public class Sprint {

    public static final String SPRINT_ID = "score-ihm-sprint";

	@Id
	@com.couchbase.client.java.repository.annotation.Id
    private String gameName = SPRINT_ID;

    @com.couchbase.client.java.repository.annotation.Field
    private Integer number;

    @com.couchbase.client.java.repository.annotation.Field
    private List<String> teams = Lists.newArrayList();

    public Sprint(Integer number) {
        this.number = number;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }
}
