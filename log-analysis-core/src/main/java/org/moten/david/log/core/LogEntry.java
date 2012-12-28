package org.moten.david.log.core;

import java.util.Map;

/**
 * Encapsulates a parsed log line.
 * 
 * @author dave
 * 
 */
class LogEntry {

	private final long time;
	private final Map<String, String> properties;
	private final String source;

	/**
	 * Constructor.
	 * 
	 * @param time
	 * @param properties
	 */
	public LogEntry(String source, long time, Map<String, String> properties) {
		this.source = source;
		this.time = time;
		this.properties = properties;
	}

	/**
	 * Returns the time in epoch ms for the log entry.
	 * 
	 * @return
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Returns the parsed properties of the log line not including the parsing
	 * of the log message.
	 * 
	 * @return
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * Returns the body of the log line.
	 * 
	 * @return
	 */
	public String getMessage() {
		return properties.get(Field.FIELD_MSG);
	}

	public String getSource() {
		return source;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LogEntry [time=");
		builder.append(time);
		builder.append(", source=");
		builder.append(source);
		builder.append(", properties=");
		builder.append(properties);
		builder.append("]");
		return builder.toString();
	}

}
