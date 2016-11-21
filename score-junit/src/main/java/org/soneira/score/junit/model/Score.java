package org.soneira.score.junit.model;

public class Score {
        private Integer iteration;

        private Integer points;

        public Score() {
        }

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
