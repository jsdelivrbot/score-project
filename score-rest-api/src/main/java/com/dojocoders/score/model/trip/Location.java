package com.dojocoders.score.model.trip;

public class Location {
	
	private int posX;
	
	private int posY;

	public Location(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}
}
