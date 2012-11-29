package org.moten.david.log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

public class LogParser {

	private final Pattern pattern = Pattern
			.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d) +(\\S+) +(\\S+) +- (.*)$");
	private final DateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS Z");

	public LogEntry parse(String line) {
		if (line == null)
			return null;
		else {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				String timestamp = matcher.group(1);
				String level = matcher.group(2);
				String logger = matcher.group(3);
				String msg = matcher.group(4);

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
				map.put("logLevel", level);
				map.put("logLogger", logger);
				map.put("logMsg", msg);

				return new LogEntry(time, map);
			} else
				return null;

		}
	}

}
