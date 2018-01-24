package com.dojocoders.score.model.trip;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "softgames", type = "trip")
public class Trip {

	@Id
	private String id;
	
	private String team;

	private List<String> messages;
	
	private Grid grid;
	
	private List<Location> course;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}
	
	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public List<Location> getCourse() {
		return course;
	}

	public void setCourse(List<Location> course) {
		this.course = course;
	}
}
