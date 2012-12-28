package org.moten.david.log.core;

import static org.moten.david.log.core.Field.FIELD_LOGGER;
import static org.moten.david.log.core.Field.FIELD_LOG_LEVEL;
import static org.moten.david.log.core.Field.FIELD_LOG_TIMESTAMP;
import static org.moten.david.log.core.Field.FIELD_METHOD;
import static org.moten.david.log.core.Field.FIELD_MSG;
import static org.moten.david.log.core.Field.FIELD_THREAD_NAME;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;

/**
 * Parses log lines. Handles multiline logs by buffering previous line then
 * checking for a pattern match against the concatenation of the previous and
 * current line.
 * 
 * @author dxm
 * 
 */
public class LogParser {

	public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss.SSS";
	private final LogParserOptions options;
	private String previousLine;
	private final MessageSplitter splitter;

	/**
	 * Constructor.
	 * 
	 * @param options
	 */
	public LogParser(LogParserOptions options) {
		this.options = options;
		splitter = new MessageSplitter(options.getMessagePattern());
	}

	/**
	 * Constructor loads from /log-parser.properties on classpath.
	 */
	public LogParser() {
		this(LogParserOptions.load());
	}

	/**
	 * Returns the parsed line as a {@link LogEntry}. Note that this method is
	 * synchronized because the {@link DateFormat} object used by it is not
	 * thread-safe. However, you can safely instantiate multiple
	 * {@link LogParser} objects and use them concurrently.
	 * 
	 * @param line
	 * @return
	 */
	public synchronized LogEntry parse(String source, String line) {
		if (line == null)
			return null;
		else {
			if (options.isMultiline() && (previousLine == null)) {
				previousLine = line;
				return null;
			} else {
				StringBuilder candidate = new StringBuilder(line);
				if (options.isMultiline()) {
					candidate.insert(0, "ZZZ");
					candidate.insert(0, previousLine);
				}
				Matcher matcher = options.getPattern().matcher(candidate);
				if (matcher.find()) {
					previousLine = null;
					return createLogEntry(source, matcher);
				} else {
					previousLine = line;
					return null;
				}
			}
		}
	}

	private LogEntry createLogEntry(String source, Matcher matcher) {
		BiMap<String, Integer> map = options.getPatternGroups();
		String timestamp = getGroup(matcher, map.get(FIELD_LOG_TIMESTAMP));
		String level = getGroup(matcher, map.get(FIELD_LOG_LEVEL));
		String logger = getGroup(matcher, map.get(FIELD_LOGGER));
		String threadName = getGroup(matcher, map.get(FIELD_THREAD_NAME));
		String msg = getGroup(matcher, map.get(FIELD_MSG));
		String method = getGroup(matcher, map.get(FIELD_METHOD));

		Long time;
		if (timestamp != null && level != null && logger != null) {
			time = parseTime(timestamp);
		} else
			time = null;

		Map<String, String> values = getValues(level, logger, threadName, msg,
				method);
		// persist the split fields from the full message
		Map<String, String> m = splitter.split(msg);
		values.putAll(m);
		values.put(Field.FIELD_SOURCE, source);
		return new LogEntry(time, values);
	}

	private Long parseTime(String timestamp) {
		Long time;
		try {
			time = options.getTimestampFormat()
					.parse(timestamp + " " + options.getTimezone()).getTime();
		} catch (ParseException e) {
			time = null;
		}
		return time;
	}

	private Map<String, String> getValues(String level, String logger,
			String threadName, String msg, String method) {
		Map<String, String> values = Maps.newHashMap();
		if (level != null)
			values.put(FIELD_LOG_LEVEL, level);
		if (logger != null)
			values.put(FIELD_LOGGER, logger);
		if (msg != null)
			values.put(FIELD_MSG, msg);
		if (threadName != null && threadName.length() > 0)
			values.put(FIELD_THREAD_NAME, threadName);
		if (method != null && method.length() > 0)
			values.put(FIELD_METHOD, method);
		return values;
	}

	private String getGroup(Matcher matcher, Integer index) {
		if (index == null)
			return null;
		return matcher.group(index);
	}

}
