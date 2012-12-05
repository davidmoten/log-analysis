package org.moten.david.log;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

public class MessageSplitter {

	private final Pattern pattern = Pattern.compile("(\\w+)=([^;^|]*)(;|\\|)");

	public Map<String, String> split(String s) {
		if (s == null || s.length() == 0)
			return Collections.emptyMap();
		else {
			Map<String, String> map = Maps.newHashMap();
			Matcher matcher = pattern.matcher(s);
			while (matcher.find()) {
				map.put(matcher.group(1), matcher.group(2).trim());
			}
			return map;
		}
	}
}
