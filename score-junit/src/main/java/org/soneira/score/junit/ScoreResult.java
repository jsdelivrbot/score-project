package org.soneira.score.junit;

import com.google.common.collect.Maps;

import java.util.Map;
public class ScoreResult {

    private String team;

    private Map<Integer, Integer> pointMap = Maps.newHashMap();

    public ScoreResult() {
    }

    public ScoreResult(String team) {
        this.team = team;
    }

    public void put(Integer sprint, Integer newScore) {
        pointMap.put(sprint, newScore);
    }

    public Map<Integer, Integer> getPointMap() {
        return this.pointMap;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

}
