package org.moten.david.log.configuration;

import java.util.List;

import com.google.common.collect.Lists;

public class Configuration {
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
}
