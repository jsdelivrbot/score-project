package com.dojocoders.score.service;

import com.dojocoders.score.model.Score;
import com.dojocoders.score.model.ScoreResult;
import com.dojocoders.score.model.Sprint;
import com.dojocoders.score.repository.ScoreResultRepository;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScoreServiceTest {

    @InjectMocks
    private ScoreService scoreService;

    @Mock
    private ScoreResultRepository repository;

    @Mock
    private SprintService sprintService;

    @Mock
    private ScoreFillerService scoreFillerService;


    @Test
    public void checkGetAllScores_withEmptyScores() {
        // Setup
        when(repository.findAll()).thenReturn(null);

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
        when(repository.findAll()).thenReturn(Arrays.asList(scoreResult,scoreResult2));

        // Test
        List<ScoreResult> result = scoreService.getAllScores();

        // Assert
        assertThat(result).hasSize(2).containsOnly(scoreResult, scoreResult2);
    }


    @Test
    public void checkGetAllScoresFilled_withEmptyScores_forFirstSprint() {
        // Setup
        Sprint sprint = new Sprint(0);
        when(sprintService.getSprint()).thenReturn(sprint);
        when(repository.findAll()).thenReturn(null);

        // Test
        List<ScoreResult> result = scoreService.getAllScoresFilled();

        // Assert
        assertThat(result).isEmpty();
    }


    @Test
    public void checkGetAllScoresFilled_withSomeUsers() {
        // Setup
        ScoreResult scoreInDataBase = Mockito.mock(ScoreResult.class);
        List<ScoreResult> scoresInDataBase = Arrays.asList(scoreInDataBase, scoreInDataBase);
        when(repository.findAll()).thenReturn(scoresInDataBase);

        Sprint sprint = new Sprint(1);
        when(sprintService.getSprint()).thenReturn(sprint);

        ScoreResult scoreFilled = Mockito.mock(ScoreResult.class);
        when(scoreFillerService.fillScores(scoreInDataBase, sprint.getNumber())).thenReturn(scoreFilled);

        // Test
        List<ScoreResult> result = scoreService.getAllScoresFilled();

        // Assert
        assertThat(result).hasSize(2).containsExactly(scoreFilled, scoreFilled);
    }

    @Test
    public void checkAddScore_firstTeam() {
        // Setup
        String teamName = "team";
        int points = 200;

        when(repository.findOne(teamName)).thenReturn(null);

        Sprint sprint = new Sprint(1);
        when(sprintService.prepareNextSprintFor(teamName)).thenReturn(sprint);

        ScoreResult scoreFilled = new ScoreResult(teamName);
        when(scoreFillerService.fillScores(isA(ScoreResult.class), eq(0))).thenReturn(scoreFilled);


        // Test
        ScoreResult result = scoreService.addScore(teamName, points);

        // Assertions
        assertThat(result.getTeam()).isEqualTo(teamName);
        Assertions.assertThat(result.getScores()).hasSize(2);
        assertThat(result.getScores().get(0).getSprint()).isEqualTo(0);
        assertThat(result.getScores().get(0).getPoints()).isEqualTo(0);
        assertThat(result.getScores().get(1).getSprint()).isEqualTo(1);
        assertThat(result.getScores().get(1).getPoints()).isEqualTo(points);
    }

    @Test
    public void checkAddScore_firstTeam_secondIteration() {
        // Setup
        String teamName = "team";
        int points = 300;

        ScoreResult scoreInDataBase = new ScoreResult(teamName);
        scoreInDataBase.getScores().add(new Score(1, 200));
        when(repository.findOne(teamName)).thenReturn(null);

        Sprint sprint = new Sprint(2);
        when(sprintService.prepareNextSprintFor(teamName)).thenReturn(sprint);

        ScoreResult scoreFilled = new ScoreResult(teamName);
        scoreFilled.getScores().add(new Score(1, 200));
        when(scoreFillerService.fillScores(isA(ScoreResult.class), eq(1))).thenReturn(scoreFilled);


        // Test
        ScoreResult result = scoreService.addScore(teamName, points);

        // Assertions
        assertThat(result.getTeam()).isEqualTo(teamName);
        Assertions.assertThat(result.getScores()).hasSize(3);
        assertThat(result.getScores().get(0).getSprint()).isEqualTo(0);
        assertThat(result.getScores().get(0).getPoints()).isEqualTo(0);
        assertThat(result.getScores().get(1).getSprint()).isEqualTo(1);
        assertThat(result.getScores().get(1).getPoints()).isEqualTo(200);
        assertThat(result.getScores().get(2).getSprint()).isEqualTo(2);
        assertThat(result.getScores().get(2).getPoints()).isEqualTo(500);
    }

    @Test
    public void checkAddScore_teamOnFifthIteration_butDidntFromPlaySecondIteration() {
        // Setup
        String teamName = "team";
        int points = 300;

        ScoreResult scoreInDataBase = new ScoreResult(teamName);
        scoreInDataBase.getScores().add(new Score(1, 200));
        when(repository.findOne(teamName)).thenReturn(null);

        Sprint sprint = new Sprint(5);
        when(sprintService.prepareNextSprintFor(teamName)).thenReturn(sprint);

        ScoreResult scoreFilled = new ScoreResult(teamName);
        scoreFilled.getScores().add(new Score(1, 200));
        scoreFilled.getScores().add(new Score(2, 200));
        scoreFilled.getScores().add(new Score(3, 200));
        scoreFilled.getScores().add(new Score(4, 200));
        when(scoreFillerService.fillScores(isA(ScoreResult.class), eq(4))).thenReturn(scoreFilled);


        // Test
        ScoreResult result = scoreService.addScore(teamName, points);

        // Assertions
        assertThat(result.getTeam()).isEqualTo(teamName);
        Assertions.assertThat(result.getScores()).hasSize(6);
        assertThat(result.getScores().get(0).getSprint()).isEqualTo(0);
        assertThat(result.getScores().get(0).getPoints()).isEqualTo(0);
        assertThat(result.getScores().get(1).getSprint()).isEqualTo(1);
        assertThat(result.getScores().get(1).getPoints()).isEqualTo(200);
        assertThat(result.getScores().get(2).getSprint()).isEqualTo(2);
        assertThat(result.getScores().get(2).getPoints()).isEqualTo(200);
        assertThat(result.getScores().get(3).getSprint()).isEqualTo(3);
        assertThat(result.getScores().get(3).getPoints()).isEqualTo(200);
        assertThat(result.getScores().get(4).getSprint()).isEqualTo(4);
        assertThat(result.getScores().get(4).getPoints()).isEqualTo(200);
        assertThat(result.getScores().get(5).getSprint()).isEqualTo(5);
        assertThat(result.getScores().get(5).getPoints()).isEqualTo(500);
    }

}