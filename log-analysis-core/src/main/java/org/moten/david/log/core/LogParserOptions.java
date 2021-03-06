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

/**
 * Options for a {@link LogParser}.
 * 
 * @author dave
 * 
 */
public class LogParserOptions {

	private final Pattern pattern;
	private final BiMap<String, Integer> patternGroups;
	private final Pattern messagePattern;

	private final DateFormat timestampFormat;
	private final String timezone;
	private final boolean multiline;

	/**
	 * Constructor.
	 * 
	 * @param pattern
	 * @param patternGroups
	 * @param messagePattern
	 * @param timestampFormat
	 * @param timezone
	 * @param multiline
	 */
	public LogParserOptions(Pattern pattern,
			BiMap<String, Integer> patternGroups, Pattern messagePattern,
			DateFormat timestampFormat, String timezone, boolean multiline) {
		super();
		this.pattern = pattern;
		this.patternGroups = patternGroups;
		this.messagePattern = messagePattern;
		this.timestampFormat = timestampFormat;
		this.timezone = timezone;
		this.multiline = multiline;
	}

	/**
	 * Constructor.
	 * 
	 * @param pattern
	 * @param patternGroups
	 * @param messagePattern
	 * @param timestampFormat
	 * @param timezone
	 * @param multiline
	 */
	public LogParserOptions(Pattern pattern,
			BiMap<String, Integer> patternGroups, Pattern messagePattern,
			String timestampFormat, String timezone, boolean multiline) {
		this(pattern, patternGroups, messagePattern,
				createDateFormat(timestampFormat), timezone, multiline);
	}

	private static LogParserOptions load(InputStream is) {
		Properties p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Pattern pattern = Pattern.compile(p.getProperty("pattern"));
		Pattern messagePattern = Pattern.compile(p
				.getProperty("message.pattern"));
		String timestampFormat = p.getProperty("timestamp.format");
		DateFormat df = createDateFormat(timestampFormat);
		String timezone = p.getProperty("timestamp.timezone");
		BiMap<String, Integer> patternGroups = createGroupMap(p
				.getProperty("pattern.groups"));
		boolean multiline = "true".equalsIgnoreCase(p.getProperty("multiline"));
		return new LogParserOptions(pattern, patternGroups, messagePattern, df,
				timezone, multiline);
	}

	private static SimpleDateFormat createDateFormat(String timestampFormat) {
		return new SimpleDateFormat(timestampFormat + " Z");
	}

	/**
	 * Returns {@link LogParser} options given default {@link Parser} and a
	 * {@link Group}.
	 * 
	 * @param defaultParser
	 * @param group
	 * @return
	 */
	public static LogParserOptions load(Parser defaultParser, Group group) {
		Parser parser = defaultParser;
		if (group.parser != null)
			parser = group.parser;

		return load(parser.pattern, parser.patternGroups,
				parser.messagePattern, parser.timestampFormat, parser.timezone,
				parser.multiline);
	}

	public static LogParserOptions load(String pPattern, String pPatternGroups,
			String pMessagePattern, String pTimestampFormat, String pTimezone,
			boolean pMultiline) {
		Pattern pattern = Pattern.compile(pPattern);
		Pattern messagePattern = Pattern.compile(pMessagePattern);
		DateFormat df = new SimpleDateFormat(pTimestampFormat);
		BiMap<String, Integer> patternGroups = createGroupMap(pPatternGroups);
		return new LogParserOptions(pattern, patternGroups, messagePattern, df,
				pTimezone, pMultiline);
	}

	/**
	 * Returns {@link LogParserOptions} from properties in
	 * /log-parser.properties on classpath.
	 * 
	 * @return
	 */
	public static LogParserOptions load() {
		return load(LogParserOptions.class
				.getResourceAsStream("/log-parser.properties"));
	}

	/**
	 * Returns the log line high level {@link Pattern} (not including the regex
	 * for extraction of key values from log message).
	 * 
	 * @return
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * Returns the positions of standard log line properties in the pattern.
	 * logTimestamp -> 1 means that logTimestamp is group 1 in the regex
	 * pattern.
	 * 
	 * @return
	 */
	public BiMap<String, Integer> getPatternGroups() {
		return patternGroups;
	}

	/**
	 * Returns {@link DateFormat} in use once the timestap format is combined
	 * with the timezone.
	 * 
	 * @return
	 */
	public DateFormat getTimestampFormat() {
		return timestampFormat;
	}

	/**
	 * Timezone for the log line timestamp part.
	 * 
	 * @return
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * Returns true if and only if the logs are split over lines like for
	 * instance default java.util.logging logs.
	 * 
	 * @return
	 */
	public boolean isMultiline() {
		return multiline;
	}

	/**
	 * Returns the message regex {@link Pattern} used to extract keys and values
	 * from the log line message.
	 * 
	 * @return
	 */
	public Pattern getMessagePattern() {
		return messagePattern;
	}

	private static BiMap<String, Integer> createGroupMap(String list) {
		BiMap<String, Integer> map = HashBiMap.create(5);
		String[] items = list.split(",");
		for (int i = 0; i < items.length; i++)
			map.put(items[i], i + 1);
		return map;
	}

}
