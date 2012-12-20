package org.moten.david.log.configuration;

import java.util.List;

import com.google.common.collect.Lists;

public class Group {
	public List<String> path = Lists.newArrayList();
	public String pattern;
	public String patternGroups;
	public String timestampFormat;
	public String timezone;
	public boolean multiline;
}
