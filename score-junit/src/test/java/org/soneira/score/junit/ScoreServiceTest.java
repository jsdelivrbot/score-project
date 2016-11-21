package org.soneira.score.junit;

import org.junit.Before;
import org.junit.Test;
import org.soneira.score.junit.model.ScoreResult;
import org.soneira.score.junit.persistence.StaticMap;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ScoreServiceTest {

    private ScoreService scoreService;

    private StaticMap persistUnit = new StaticMap();

    @Before
    public void initSevice() {
        persistUnit.clear();
        scoreService = new ScoreService(persistUnit);
    }


    @Test
    public void checkGetAllScores_withEmptyScores() {
        // Test
        List<ScoreResult> result = scoreService.getAllScores();

        // Assert
        assertThat(result).isEmpty();
    }


    @Test
    public void checkGetAllScores_withSomeUsers() {
        // Setup
        String teamName = "team";
        String teamName2 = "team2";
        ScoreResult scoreResult = new ScoreResult(teamName);
        ScoreResult scoreResult2 = new ScoreResult(teamName2);
        persistUnit.putScore(teamName, scoreResult);
        persistUnit.putScore(teamName2, scoreResult2);

        // Test
        List<ScoreResult> result = scoreService.getAllScores();

        // Assert
        assertThat(result).hasSize(2).containsOnly(scoreResult, scoreResult2);
    }

    @Test
    public void checkAddScore_newTeam() {
        // Setup
        String teamName = "team";
        int points = 200;


        // Test
        ScoreResult result = scoreService.addScore(teamName, points);

        // Assertions
        assertThat(result.getTeam()).isEqualTo(teamName);
        assertThat(result.getScores()).hasSize(1);
        assertThat(result.getScores().get(0).getIteration()).isEqualTo(1);
        assertThat(result.getScores().get(0).getPoints()).isEqualTo(points);
        assertThat(scoreService.getAllScores()).hasSize(1).containsOnly(result);
    }

    @Test
    public void checkAddScore_teamOn2ndIteration() {
        // Setup
        String teamName = "team";
        int pointsIt1 = 200;
        int pointsIt2 = 220;


        // Test
        scoreService.addScore(teamName, pointsIt1);
        ScoreResult result = scoreService.addScore(teamName, pointsIt2);

        // Assertions
        assertThat(result.getTeam()).isEqualTo(teamName);
        assertThat(result.getScores()).hasSize(2);
        assertThat(result.getScores().get(0).getIteration()).isEqualTo(1);
        assertThat(result.getScores().get(0).getPoints()).isEqualTo(pointsIt1);
        assertThat(result.getScores().get(1).getIteration()).isEqualTo(2);
        assertThat(result.getScores().get(1).getPoints()).isEqualTo(pointsIt1 + pointsIt2);
        assertThat(scoreService.getAllScores()).hasSize(1).containsOnly(result);
    }

    @Test
    public void checkAddScore_multipleTeams() {
        // Setup
        String teamName = "team";
        String teamName2 = "team2";
        int pointsTeam1 = 200;
        int pointsTeam2 = 220;


        // Test
        ScoreResult resultTeam1 = scoreService.addScore(teamName, pointsTeam1);
        ScoreResult resultTeam2 = scoreService.addScore(teamName2, pointsTeam2);
        List<ScoreResult> allScores = scoreService.getAllScores();

        // Assertions
        assertThat(allScores).hasSize(2).containsOnly(resultTeam1, resultTeam2);
        assertThat(resultTeam1.getTeam()).isEqualTo(teamName);
        assertThat(resultTeam1.getScores()).hasSize(1);
        assertThat(resultTeam1.getScores().get(0).getIteration()).isEqualTo(1);
        assertThat(resultTeam1.getScores().get(0).getPoints()).isEqualTo(pointsTeam1);
        assertThat(resultTeam2.getTeam()).isEqualTo(teamName2);
        assertThat(resultTeam2.getScores()).hasSize(1);
        assertThat(resultTeam2.getScores().get(0).getIteration()).isEqualTo(1);
        assertThat(resultTeam2.getScores().get(0).getPoints()).isEqualTo(pointsTeam2);
    }
}