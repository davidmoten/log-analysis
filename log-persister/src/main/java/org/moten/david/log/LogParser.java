package org.moten.david.log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

public class LogParser {

	public static final String FIELD_MSG = "logMsg";
	public static final String FIELD_LOGGER = "logLogger";
	public static final String FIELD_LOG_LEVEL = "logLevel";
	public static final String FIELD_LOG_TIMESTAMP = "logTimestamp";

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	static final String FIELD_THREAD_NAME = "threadName";
	private final Pattern pattern = Pattern
			.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d) +(\\S+) +(\\S+) +(\\S+)? ?- (.*)$");
	private final BiMap<String, Integer> map = createGroupMap();
	private final DateFormat df = new SimpleDateFormat(DATE_FORMAT + " Z");

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
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				String timestamp = matcher.group(map.get(FIELD_LOG_TIMESTAMP));
				String level = matcher.group(map.get(FIELD_LOG_LEVEL));
				String logger = matcher.group(map.get(FIELD_LOGGER));
				String threadName = matcher.group(map.get(FIELD_THREAD_NAME));
				String msg = matcher.group(map.get(FIELD_MSG));

				Long time;
				if (timestamp != null && level != null && logger != null) {
					try {
						time = df.parse(timestamp + " UTC").getTime();
					} catch (ParseException e) {
						time = null;
					}
				} else
					time = null;

				Map<String, String> map = Maps.newHashMap();
				map.put(FIELD_LOG_LEVEL, level);
				map.put(FIELD_LOGGER, logger);
				map.put(FIELD_MSG, msg);
				if (threadName != null && threadName.length() > 0)
					map.put(FIELD_THREAD_NAME, threadName);

				return new LogEntry(time, map);
			} else
				return null;

		}
	}

	private BiMap<String, Integer> createGroupMap() {
		BiMap<String, Integer> map = HashBiMap.create(5);
		map.put(FIELD_LOG_TIMESTAMP, 1);
		map.put(FIELD_LOG_LEVEL, 2);
		map.put(FIELD_LOGGER, 3);
		map.put(FIELD_THREAD_NAME, 4);
		map.put(FIELD_MSG, 5);

		return map;
	}
}
