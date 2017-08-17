package com.dojocoders.score.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.dojocoders.score.model.Score;
import com.dojocoders.score.model.ScoreResult;

public class ScoreFillerServiceTest {

	private ScoreFillerService scoreFillerService = new ScoreFillerService();

	@Test
	public void checkFillScores_withEmptyScores_forFirstSprint() {
		// Test
		ScoreResult result = scoreFillerService.fillScores(new ScoreResult("team"), 0);

		// Assert
		assertThat(result.getScores()).hasSize(1);
		assertThat(result.getScores().get(0).getSprint()).isEqualTo(0);
		assertThat(result.getScores().get(0).getPoints()).isEqualTo(0);
	}

	@Test
	public void checkFillScores_withSomeScoresButNotAll_CompleteOnTheEnd() {

		ScoreResult scoreResult = new ScoreResult("team");
		scoreResult.getScores().add(new Score(0, 0));
		scoreResult.getScores().add(new Score(1, 20));

		// Test
		ScoreResult result = scoreFillerService.fillScores(scoreResult, 4);

		// Assert
		assertThat(result.getScores()).hasSize(5);
		assertThat(result.getScores().get(0).getSprint()).isEqualTo(0);
		assertThat(result.getScores().get(0).getPoints()).isEqualTo(0);
		assertThat(result.getScores().get(1).getSprint()).isEqualTo(1);
		assertThat(result.getScores().get(1).getPoints()).isEqualTo(20);
		assertThat(result.getScores().get(2).getSprint()).isEqualTo(2);
		assertThat(result.getScores().get(2).getPoints()).isEqualTo(20);
		assertThat(result.getScores().get(3).getSprint()).isEqualTo(3);
		assertThat(result.getScores().get(3).getPoints()).isEqualTo(20);
		assertThat(result.getScores().get(4).getSprint()).isEqualTo(4);
		assertThat(result.getScores().get(4).getPoints()).isEqualTo(20);
	}
}