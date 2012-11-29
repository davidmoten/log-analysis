package org.moten.david.log;

import java.util.Map;

public class LogEntry {

	private final long time;
	private final Map<String, String> properties;

	public LogEntry(long time, Map<String, String> properties) {
		super();
		this.time = time;
		this.properties = properties;
	}

	public long getTime() {
		return time;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LogEntry [time=");
		builder.append(time);
		builder.append(", properties=");
		builder.append(properties);
		builder.append("]");
		return builder.toString();
	}

}
