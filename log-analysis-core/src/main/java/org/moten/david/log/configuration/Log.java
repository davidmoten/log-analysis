package org.moten.david.log.configuration;

public class Log {
	public String path;
	public boolean watch;

	public Log(String path, boolean watch) {
		super();
		this.path = path;
		this.watch = watch;
	}

	public Log() {
		// no-args constructor required by jaxb
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Log [path=");
		builder.append(path);
		builder.append(", watch=");
		builder.append(watch);
		builder.append("]");
		return builder.toString();
	}

}
