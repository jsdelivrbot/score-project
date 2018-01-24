package com.dojocoders.score.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.dojocoders.score.model.Sprint;
import com.dojocoders.score.repository.SprintRepository;

@RunWith(MockitoJUnitRunner.class)
public class SprintServiceTest {

	@InjectMocks
	private SprintService sprintService;

	@Mock
	private SprintRepository repository;

	@Test
	public void checkGetSprint_noSprintInDataBase() {

		// Test
		Sprint result = sprintService.getSprint();

		// Assert
		assertThat(result.getNumber()).isEqualTo(1);
		assertThat(result.getTeams()).isEmpty();

	}

	@Test
	public void checkGetSprint_secondSprint() {

		Sprint sprint = new Sprint(2);
		when(repository.findById(Sprint.SPRINT_ID)).thenReturn(Optional.of(sprint));

		// Test
		Sprint result = sprintService.getSprint();

		// Assert
		assertThat(result).isEqualTo(sprint);

	}

	@Test
	public void checkGetPrepareNextSprint_teamAlreadyPlayLastSprint() {

		String team = "teamName";
		String otherTeam = "otherTeamName";

		Sprint sprint = new Sprint(2);
		sprint.getTeams().add(team);
		sprint.getTeams().add(otherTeam);
		when(repository.findById(Sprint.SPRINT_ID)).thenReturn(Optional.of(sprint));

		// Test
		Sprint result = sprintService.prepareNextSprintFor(team);

		// Assert
		assertThat(result.getNumber()).isEqualTo(3);
		assertThat(result.getTeams()).hasSize(1).containsOnly(team);
	}

	@Test
	public void checkGetPrepareNextSprint_teamDidntPlayLastSprint() {

		String team = "teamName";
		String otherTeam = "otherTeamName";

		Sprint sprint = new Sprint(2);
		sprint.getTeams().add(otherTeam);
		when(repository.findById(Sprint.SPRINT_ID)).thenReturn(Optional.of(sprint));

		// Test
		Sprint result = sprintService.prepareNextSprintFor(team);

		// Assert
		assertThat(result.getNumber()).isEqualTo(2);
		assertThat(result.getTeams()).hasSize(2).containsOnly(team, otherTeam);
	}

}