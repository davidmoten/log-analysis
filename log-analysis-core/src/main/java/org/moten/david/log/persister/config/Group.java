package org.moten.david.log.persister.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.Lists;

/**
 * A group of log files and a parser definition.
 * 
 * @author dave
 * 
 */
public class Group {

	@XmlElement(required = false)
	public List<Log> log = Lists.newArrayList();

	@XmlElement(required = false)
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
