package org.moten.david.log.config;

import java.util.List;

public class Options {

	private String defaultPattern;
	private String defaultExtractions;
	private List<Log> log;

	public Options() {
		this(null, null, null);
	}

	public Options(String defaultPattern, String defaultExtractions,
			List<Log> log) {
		super();
		this.defaultPattern = defaultPattern;
		this.defaultExtractions = defaultExtractions;
		this.log = log;
	}

	public String getDefaultPattern() {
		return defaultPattern;
	}

	public void setDefaultPattern(String defaultPattern) {
		this.defaultPattern = defaultPattern;
	}

	public String getDefaultExtractions() {
		return defaultExtractions;
	}

	public void setDefaultExtractions(String defaultExtractions) {
		this.defaultExtractions = defaultExtractions;
	}

	public List<Log> getLog() {
		return log;
	}

	public void setLog(List<Log> log) {
		this.log = log;
	}

}
