package org.soneira.score;

public class Rank {

    private final String team;

    private final Integer points;

    public Rank(String team, Integer points) {
        this.team = team;
        this.points = points;
    }

    public String getTeam() {
        return team;
    }

    public Integer getPoints() {
        return points;
    }

}
