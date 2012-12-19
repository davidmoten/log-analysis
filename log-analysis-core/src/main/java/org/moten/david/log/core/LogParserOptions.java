package org.moten.david.log.core;

import java.text.DateFormat;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.google.common.collect.BiMap;

public class LogParserOptions {

	public LogParserOptions(Pattern pattern,
			BiMap<String, Integer> patternGroups, DateFormat timestampFormat,
			TimeZone timezone, boolean multiline) {
		super();
		this.pattern = pattern;
		this.patternGroups = patternGroups;
		this.timestampFormat = timestampFormat;
		this.timezone = timezone;
		this.multiline = multiline;
	}

	private final Pattern pattern;
	private final BiMap<String, Integer> patternGroups;
	private final DateFormat timestampFormat;
	private final TimeZone timezone;
	private final boolean multiline;

}
