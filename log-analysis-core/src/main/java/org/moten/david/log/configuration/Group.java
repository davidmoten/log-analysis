package org.moten.david.log.configuration;

import java.util.List;

import com.google.common.collect.Lists;

public class Group {
	public List<Log> log = Lists.newArrayList();
	public Parser parser;

	public Group(List<Log> log, Parser parser) {
		super();
		this.log = log;
		this.parser = parser;
	}

	public Group(List<Log> log) {
		this(log, null);
	}

	public Group() {
		// no-args constructor required by jaxb
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Group [log=");
		builder.append(log);
		builder.append(", parser=");
		builder.append(parser);
		builder.append("]");
		return builder.toString();
	}

}
