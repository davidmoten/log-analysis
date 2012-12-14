package org.moten.david.log.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class LogReader {

	private final BufferedReader reader;
	private final LogParser parser;

	public LogReader(InputStream in, LogParser parser) {
		this.parser = parser;
		reader = new BufferedReader(new InputStreamReader(in,
				Charset.forName("UTF-8")));
	}

	public LogEntry next() {
		String line;
		try {
			line = reader.readLine();
			if (line == null)
				return null;
			else {
				LogEntry entry = null;
				while (line != null && (entry = parser.parse(line)) == null) {
					line = reader.readLine();
				}
				return entry;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
