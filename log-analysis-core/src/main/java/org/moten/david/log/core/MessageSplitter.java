package org.moten.david.log.core;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

/**
 * Extracts key=value pairs from a log line message.
 * 
 * @author dave
 * 
 */
public class MessageSplitter {

	public static final String MESSAGE_PATTERN_DEFAULT = "(\\b[a-zA-Z](?:\\w| )*)=([^;|,]*)(;|\\||,|$)";
	private final Pattern pattern;

	public MessageSplitter() {
		this(Pattern.compile(MESSAGE_PATTERN_DEFAULT));
	}

	public MessageSplitter(Pattern pattern) {
		this.pattern = pattern;
	}

	public Map<String, String> split(String s) {
		if (s == null || s.length() == 0)
			return Collections.emptyMap();
		else {
			Map<String, String> map = Maps.newHashMap();
			Matcher matcher = pattern.matcher(s);
			while (matcher.find()) {
				map.put(matcher.group(1).trim(), matcher.group(2).trim());
			}
			return map;
		}
	}
}
