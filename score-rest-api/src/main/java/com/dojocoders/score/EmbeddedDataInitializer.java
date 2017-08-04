package com.dojocoders.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.dojocoders.score.api.ScoreController;
import com.dojocoders.score.api.TeamController;
import com.dojocoders.score.api.TripController;
import com.dojocoders.score.model.trip.Grid;
import com.dojocoders.score.model.trip.Item;
import com.dojocoders.score.model.trip.Location;
import com.dojocoders.score.model.trip.Trip;
import com.google.common.collect.Lists;

@Component
@Profile("embedded-persistence")
public class EmbeddedDataInitializer {

	private static String ITEMS = "AaMmEeBb"; // Asteorid/Meteorite, Enemy, Bomb
	
	@Autowired
	private TeamController teamController;
	
	@Autowired
	private ScoreController scoreController;
	
	@Autowired
	private TripController tripController;
	
	@PostConstruct
	public void init() {
		setupTeam("Atlanta Falcons");
		setupTeam("LA Warriors");
		setupTeam("Dallas Cowboys");
		setupTeam("Philadelphia Snakes");
	}
	
	private void setupTeam(String name) {
		teamController.addTeam(name);
		scoreController.addScoreTeamPoints(name, name.length());
		setupTrip(name);
	}
	
	private void setupTrip(String team) {
		Trip trip = new Trip();
		trip.setMessages(buildMessages(team));
		trip.setGrid(buildGrid(team));
		trip.setTeam(team);
		Date date = new Date();
		trip.setId(String.valueOf(date.getTime()));
		tripController.addTrip(trip);
	}
	
	private List<String> buildMessages(String team) {
		List<String> msg = new ArrayList<String>();
		msg.add(team + "! Gonna crush you !");
		return msg;
	}
	
	private Grid buildGrid(String team) {
		Grid grid = new Grid();
		grid.setHeight(team.length() * 2);
		grid.setWidth(team.length() * 2);
		grid.setContent(buildGridContent(team));
		return grid;
	}
	
	private List<Item> buildGridContent(String team) {
		return Arrays.stream(team.split("")) //
			.filter(letter -> letterIsAnItem(letter))
			.map(letter -> buildItem(letter, team))
			.collect(Collectors.toList());
	}
	
	private boolean letterIsAnItem(String letter) {
		return (ITEMS.indexOf(letter) > -1);
	}
	
	private Item buildItem(String letter, String team) {
		Double x = new Double(Math.random() * team.length() * 10);
		Double y = new Double(Math.random() * team.length() * 10);
		Location position = new Location(x.intValue(), y.intValue());

		return new Item(letter.toUpperCase(), position);
	}
	
}


 