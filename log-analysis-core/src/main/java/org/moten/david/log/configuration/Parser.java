package org.moten.david.log.configuration;

public class Parser {
	public String pattern;
	public String patternGroups;
	public String timestampFormat;
	public String timezone;
	public boolean multiline;

	public Parser(String pattern, String patternGroups, String timestampFormat,
			String timezone, boolean multiline) {
		super();
		this.pattern = pattern;
		this.patternGroups = patternGroups;
		this.timestampFormat = timestampFormat;
		this.timezone = timezone;
		this.multiline = multiline;
	}

	public Parser() {
		// required for jaxb
	}
}
