package org.moten.david.log.configuration;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.Lists;

/**
 * Configuration for log-persister.
 * 
 * @author dave
 * 
 */
public class Configuration {
	@XmlElement(required = true)
	public String databaseUrl = "remote:localhost/logs";
	@XmlElement(required = false)
	public Parser parser;
	@XmlElement(required = true)
	public List<Group> group = Lists.newArrayList();

	/**
	 * Constructor.
	 * 
	 * @param parser
	 * @param group
	 */
	public Configuration(Parser parser, List<Group> group) {
		super();
		this.parser = parser;
		this.group = group;
	}

	/**
	 * Constructor.
	 */
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
