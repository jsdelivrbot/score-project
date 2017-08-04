package com.dojocoders.score.model.trip;

import java.util.List;

public class Grid {

	private int width;
	
	private int height;
	
	private List<Item> content; 
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<Item> getContent() {
		return content;
	}

	public void setContent(List<Item> content) {
		this.content = content;
	}

}
