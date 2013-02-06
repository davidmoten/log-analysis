package org.moten.david.log.core;

import java.util.Map;

public class Util {
	public static String getString(Map<String, String> properties) {
		StringBuilder s = new StringBuilder();
		s.append(properties.get(Field.LEVEL));
		s.append(" ");
		s.append(properties.get(Field.LOGGER));
		s.append(" ");
		s.append(properties.get(Field.METHOD));
		s.append(" ");
		s.append(properties.get(Field.MSG));
		return s.toString();
	}
}
