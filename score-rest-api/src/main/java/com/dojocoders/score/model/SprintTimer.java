package com.dojocoders.score.model;

public class SprintTimer {

	private int countdown = 0;

	private boolean started;

	public int getCountdown() {
		return countdown;
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
}
