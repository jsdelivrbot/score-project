package com.dojocoders.score.junit;

import org.fest.util.Strings;

import com.dojocoders.score.junit.persistence.TestPersistUnit;
import com.dojocoders.score.junit.persistence.TestStaticMap;

public class TestConfiguration {

	public static final String REST_API_URL_PARAMETER = "score.rest-api.url";
	public static final String TEAM_PARAMETER = "team";
	public static final String BONUS_POINTS_PARAMETER = "bonus";
	public static final String MALUS_POINTS_PARAMETER = "malus";
	public static final String IMPLEMENTATION_SUBPACKAGE_PARAMETER = "impl.subpackage";

	public static final Class<? extends TestPersistUnit> DEFAULT_TEST_PERSISTENCE_CLASS = TestStaticMap.class;
	public static final int DEFAULT_METHOD_POINTS = 10;
	public final static int DEFAULT_BONUS_POINTS = 0;
	public final static int DEFAULT_MALUS_POINTS = 0;
	public final static String DEFAULT_TEAM = "default_team";
	public final static String DEFAULT_REST_API_URL = "http://localhost:8080";
	public final static String DEFAULT_IMPL_SUBPACKAGE = "";

	public static String getRestApiUrl() {
		return getProperty(REST_API_URL_PARAMETER, DEFAULT_REST_API_URL);
	}

	public static String getTeam() {
		return getProperty(TEAM_PARAMETER, DEFAULT_TEAM);
	}

	public static String getImplementationSubpackage() {
		return getProperty(IMPLEMENTATION_SUBPACKAGE_PARAMETER, DEFAULT_IMPL_SUBPACKAGE);
	}

	public static Integer getBonus() {
		return getProperty(BONUS_POINTS_PARAMETER, DEFAULT_BONUS_POINTS);
	}

	public static Integer getMalus() {
		return getProperty(MALUS_POINTS_PARAMETER, DEFAULT_MALUS_POINTS);
	}

	private static int getProperty(String key, int defaultValue) {
		return Integer.valueOf(getProperty(key, Integer.toString(defaultValue)));
	}

	private static String getProperty(String key, String defaultValue) {
		return Strings.isNullOrEmpty(System.getProperty(key)) ? defaultValue : System.getProperty(key);
	}
}
