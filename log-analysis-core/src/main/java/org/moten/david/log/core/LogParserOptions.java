package org.moten.david.log.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.regex.Pattern;

import org.moten.david.log.persister.config.Group;
import org.moten.david.log.persister.config.Parser;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class LogParserOptions {

	private final Pattern pattern;
	private final BiMap<String, Integer> patternGroups;
	private final DateFormat timestampFormat;
	private final String timezone;
	private final boolean multiline;

	public LogParserOptions(Pattern pattern,
			BiMap<String, Integer> patternGroups, DateFormat timestampFormat,
			String timezone, boolean multiline) {
		super();
		this.pattern = pattern;
		this.patternGroups = patternGroups;
		this.timestampFormat = timestampFormat;
		this.timezone = timezone;
		this.multiline = multiline;
	}

	public static LogParserOptions load(InputStream is) {
		Properties p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Pattern pattern = Pattern.compile(p.getProperty("pattern"));
		String timestampFormat = p.getProperty("timestamp.format");
		DateFormat df = new SimpleDateFormat(timestampFormat + " Z");
		String timezone = p.getProperty("timestamp.timezone");
		BiMap<String, Integer> patternGroups = createGroupMap(p
				.getProperty("pattern.groups"));
		boolean multiline = "true".equalsIgnoreCase(p.getProperty("multiline"));
		return new LogParserOptions(pattern, patternGroups, df, timezone,
				multiline);
	}

	public static LogParserOptions load(Parser defaultParsing, Group group) {
		Parser parsing = defaultParsing;
		if (group.parser != null)
			parsing = group.parser;

		Pattern pattern = Pattern.compile(parsing.pattern);
		DateFormat df = new SimpleDateFormat(parsing.timestampFormat);
		BiMap<String, Integer> patternGroups = createGroupMap(parsing.patternGroups);
		return new LogParserOptions(pattern, patternGroups, df,
				parsing.timezone, parsing.multiline);
	}

	public static LogParserOptions load() {
		return load(LogParserOptions.class
				.getResourceAsStream("/log-parser.properties"));
	}

	public Pattern getPattern() {
		return pattern;
	}

	public BiMap<String, Integer> getPatternGroups() {
		return patternGroups;
	}

	public DateFormat getTimestampFormat() {
		return timestampFormat;
	}

	public String getTimezone() {
		return timezone;
	}

	public boolean isMultiline() {
		return multiline;
	}

	private static BiMap<String, Integer> createGroupMap(String list) {
		BiMap<String, Integer> map = HashBiMap.create(5);
		String[] items = list.split(",");
		for (int i = 0; i < items.length; i++)
			map.put(items[i], i + 1);
		return map;
	}
}
