package org.moten.david.log.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;

/**
 * 
 * @author dxm
 * 
 */
public class LogParser {

	public static final String FIELD_MSG = "logMsg";
	public static final String FIELD_LOGGER = "logLogger";
	public static final String FIELD_LOG_LEVEL = "logLevel";
	public static final String FIELD_LOG_TIMESTAMP = "logTimestamp";
	public static final String FIELD_THREAD_NAME = "threadName";
	public static final String FIELD_METHOD = "method";

	public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss.SSS";
	private final LogParserOptions options;

	public LogParser(LogParserOptions options) {
		this.options = options;
	}

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
	public synchronized LogEntry parse(String line) {
		if (line == null)
			return null;
		else {
			Matcher matcher = options.getPattern().matcher(line);
			if (matcher.find()) {
				BiMap<String, Integer> map = options.getPatternGroups();
				String timestamp = getGroup(matcher,
						map.get(FIELD_LOG_TIMESTAMP));
				String level = getGroup(matcher, map.get(FIELD_LOG_LEVEL));
				String logger = getGroup(matcher, map.get(FIELD_LOGGER));
				String threadName = getGroup(matcher,
						map.get(FIELD_THREAD_NAME));
				String msg = getGroup(matcher, map.get(FIELD_MSG));
				String method = getGroup(matcher, map.get(FIELD_METHOD));

				Long time;
				if (timestamp != null && level != null && logger != null) {
					try {
						time = options.getTimestampFormat()
								.parse(timestamp + " " + options.getTimezone())
								.getTime();
					} catch (ParseException e) {
						time = null;
					}
				} else
					time = null;

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

				return new LogEntry(time, values);
			} else
				return null;

		}
	}

	private String getGroup(Matcher matcher, Integer index) {
		if (index == null)
			return null;
		return matcher.group(index);
	}

}
