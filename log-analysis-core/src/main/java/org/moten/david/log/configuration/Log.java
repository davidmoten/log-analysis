package org.moten.david.log.configuration;

/**
 * A log file to parse and optionally watch.
 * 
 * @author dave
 * 
 */
public class Log {
	public String path;
	public boolean watch;

	/**
	 * Constructor.
	 * 
	 * @param path
	 * @param watch
	 */
	public Log(String path, boolean watch) {
		super();
		this.path = path;
		this.watch = watch;
	}

	/**
	 * Constructor.
	 */
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
