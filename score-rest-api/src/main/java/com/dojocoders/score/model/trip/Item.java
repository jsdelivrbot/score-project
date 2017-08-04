package com.dojocoders.score.model.trip;

public class Item {
	
	private String object;
	
	private Location location;

	public Item(String object, Location location) {
		this.object = object;
		this.location = location;
	}
	
	public String getObject() {
		return object;
	}

	public Location getLocation() {
		return location;
	}

}
