package org.moten.david.log.config;

public class Log {

	private String name;
	private String path;
	private String pattern;
	private String extractions;

	public Log() {
		this(null, null, null, null);
	}

	public Log(String name, String path) {
		this(name, path, null, null);
	}

	public Log(String name, String path, String pattern, String extractions) {
		super();
		this.name = name;
		this.path = path;
		this.pattern = pattern;
		this.extractions = extractions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getExtractions() {
		return extractions;
	}

	public void setExtractions(String extractions) {
		this.extractions = extractions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Log [name=");
		builder.append(name);
		builder.append(", path=");
		builder.append(path);
		builder.append(", pattern=");
		builder.append(pattern);
		builder.append(", extractions=");
		builder.append(extractions);
		builder.append("]");
		return builder.toString();
	}

}
