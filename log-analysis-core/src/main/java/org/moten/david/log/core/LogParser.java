package org.moten.david.log.core;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Properties;
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
	public static final String FIELD_THREAD_NAME = "threadName";

	public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss.SSS";
	private final String dateFormat;
	private final String timezone;

	private final Pattern pattern;
	private final BiMap<String, Integer> map;
	private final DateFormat df;

	public LogParser() {
		Properties p = new Properties();
		try {
			p.load(LogParser.class.getResourceAsStream(System.getProperty(
					"logParserConfig", "/log-parser.properties")));
			pattern = Pattern.compile(p.getProperty("pattern"));
			dateFormat = p.getProperty("timestamp.format");
			df = new SimpleDateFormat(dateFormat + " Z");
			timezone = p.getProperty("timestamp.timezone");
			map = createGroupMap(p.getProperty("pattern.groups"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
						time = df.parse(timestamp + " " + timezone).getTime();
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

	private BiMap<String, Integer> createGroupMap(String list) {
		BiMap<String, Integer> map = HashBiMap.create(5);
		String[] items = list.split(",");
		for (int i = 0; i < items.length; i++)
			map.put(items[i], i + 1);
		return map;
	}
}
