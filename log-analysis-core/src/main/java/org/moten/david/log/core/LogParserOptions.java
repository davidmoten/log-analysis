package org.moten.david.log.core;

import com.google.common.collect.BiMap;

public class LogParserOptions {

	private final String pattern;
	private final String patternGroups;
	private final String timestampFormat;
	private final String timezone;
	private final boolean multiline;
	private final BiMap<String, Integer> groupPositions;

	public LogParserOptions(String pattern, String patternGroups,
			String timestampFormat, String timezone, boolean multiline,
			BiMap<String, Integer> groupPositions) {
		super();
		this.pattern = pattern;
		this.patternGroups = patternGroups;
		this.timestampFormat = timestampFormat;
		this.timezone = timezone;
		this.multiline = multiline;
		this.groupPositions = groupPositions;
	}

	public String getPattern() {
		return pattern;
	}

	public String getPatternGroups() {
		return patternGroups;
	}

	public String getTimestampFormat() {
		return timestampFormat;
	}

	public String getTimezone() {
		return timezone;
	}

	public boolean isMultiline() {
		return multiline;
	}

	public BiMap<String, Integer> getGroupPositions() {
		return groupPositions;
	}

}
