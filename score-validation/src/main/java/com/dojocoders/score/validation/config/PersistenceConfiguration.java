package com.dojocoders.score.validation.config;

import org.fest.util.Strings;

public class PersistenceConfiguration {

	public static final String TEAM_PARAMETER = "team";
	public static final String DEFAULT_TEAM = "default_team";

	public String getTeam() {
		return getProperty(TEAM_PARAMETER, DEFAULT_TEAM);
	}

	protected static String getProperty(String key, String defaultValue) {
		return Strings.isNullOrEmpty(System.getProperty(key)) ? defaultValue : System.getProperty(key);
	}
}
