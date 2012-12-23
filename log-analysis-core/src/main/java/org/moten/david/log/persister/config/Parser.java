package org.moten.david.log.persister.config;

/**
 * 
 * Parser options.
 * 
 * @author dave
 * 
 */
public class Parser {
	public String pattern;
	public String patternGroups;
	public String messagePattern;
	public String timestampFormat;
	public String timezone;
	public boolean multiline;

	/**
	 * Parser configuration.
	 * 
	 * @param pattern
	 * @param patternGroups
	 * @param timestampFormat
	 * @param timezone
	 * @param multiline
	 */
	public Parser(String pattern, String patternGroups, String messagePattern,
			String timestampFormat, String timezone, boolean multiline) {
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
	 */
	public Parser() {
		// required for jaxb
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Parser [pattern=");
		builder.append(pattern);
		builder.append(", patternGroups=");
		builder.append(patternGroups);
		builder.append(", messagePattern=");
		builder.append(messagePattern);
		builder.append(", timestampFormat=");
		builder.append(timestampFormat);
		builder.append(", timezone=");
		builder.append(timezone);
		builder.append(", multiline=");
		builder.append(multiline);
		builder.append("]");
		return builder.toString();
	}

}
