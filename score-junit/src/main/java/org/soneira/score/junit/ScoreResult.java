package org.soneira.score.junit;

import com.google.common.collect.Lists;

import java.util.List;
public class ScoreResult {

    private String team;

    private List<Score> scores = Lists.newArrayList();

    public ScoreResult() {
    }

    public ScoreResult(String team) {
        this.team = team;
    }

    public void put(Integer sprint, Integer newScore) {
        scores.add(new Score(sprint, newScore));
    }

    public List<Score> getScores() {
        return this.scores;
    }

    public String getTeam() {
        return team;
    }

    public class Score {
        private Integer iteration;

        private Integer points;

        public Score(Integer iteration, Integer points) {
            this.iteration = iteration;
            this.points = points;
        }

        public Integer getIteration() {
            return iteration;
        }

        public Integer getPoints() {
            return points;
        }
    }

}