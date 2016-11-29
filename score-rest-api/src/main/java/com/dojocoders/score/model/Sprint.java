package com.dojocoders.score.model;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import com.google.common.collect.Lists;
import org.springframework.data.couchbase.core.mapping.Document;

import java.util.List;

@Document
public class Sprint {

    public static final String SPRINT_ID = "score-ihm-sprint";

    @Id
    private String gameName = SPRINT_ID;

    @Field
    private Integer number = 1;

    @Field
    private List<String> teams = Lists.newArrayList();

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
