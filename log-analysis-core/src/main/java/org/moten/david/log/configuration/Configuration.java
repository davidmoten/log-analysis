package org.moten.david.log.configuration;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * @author dave
 * 
 */
public class Configuration {
	public String databaseUrl = "remote:localhost/logs";
	public Parser parser;
	public List<Group> group = Lists.newArrayList();

	public Configuration(Parser parser, List<Group> group) {
		super();
		this.parser = parser;
		this.group = group;
	}

	public Configuration() {
		// no-args constructor required by jaxb
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Configuration [databaseUrl=");
		builder.append(databaseUrl);
		builder.append(", parser=");
		builder.append(parser);
		builder.append(", group=");
		builder.append(group);
		builder.append("]");
		return builder.toString();
	}

}
