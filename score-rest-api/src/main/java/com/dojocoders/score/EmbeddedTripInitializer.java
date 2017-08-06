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

import com.dojocoders.score.api.TeamController;
import com.dojocoders.score.api.TripController;
import com.dojocoders.score.model.trip.Grid;
import com.dojocoders.score.model.trip.Item;
import com.dojocoders.score.model.trip.Location;
import com.dojocoders.score.model.trip.Trip;

@Component
@Profile("embedded-persistence")
public class EmbeddedTripInitializer {

	private static String ITEMS = "AaMmEeBb"; // Asteorid/Meteorite, Enemy, Bomb
	
	@Autowired
	private TeamController teamController;
	
	@Autowired
	private TripController tripController;
	
	@SuppressWarnings("unused") // Injection ensures that teams are initialized before trips
	@Autowired
	private EmbeddedDataInitializer dataInitializer; 
	
	@PostConstruct
	public void init() {
		setupTrips();
	}
	
	private void setupTrips() {
		teamController.teams().forEach(team -> setupTrip(team));
	}
	
	private void setupTrip(String team) {
		Date date = new Date();
		Trip trip = new Trip();
		trip.setMessages(buildMessages(team));
		trip.setGrid(buildGrid(team + team));
		trip.setTeam(team);
		trip.setId(String.valueOf(date.getTime()));
		trip.setCourse(buildLocations(team + team));
		tripController.addTrip(trip);
	}
	
	private List<String> buildMessages(String team) {
		List<String> msg = new ArrayList<String>();
		msg.add(team + "! Gonna crush you !");
		msg.add("Enemy is trying to pass !");
		return msg;
	}
	
	private List<Location> buildLocations(String team) {
		List<Location> locations = new ArrayList<Location>();
		int posMax = team.length();
		int maxXorY = 0;
		int posX = 0;
		int posY = 0;
		Double random;
		while (maxXorY < posMax) {
			random = (Math.random() * 2) + 1;
			if (random.intValue() > 1) {
				posX++;
			} else {
				posY++;
			}
			locations.add(new Location(posX, posY));
			maxXorY = (posX > posY) ? posX : posY;
		}
		return locations;
	}
	
	private Grid buildGrid(String team) {
		Grid grid = new Grid();
		grid.setHeight(team.length() + 1);
		grid.setWidth(team.length() + 1);
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
		Double x = new Double(Math.random() * team.length());
		Double y = new Double(Math.random() * team.length());
		Location position = new Location(x.intValue(), y.intValue());

		return new Item(letter.toUpperCase(), position);
	}
	
}

