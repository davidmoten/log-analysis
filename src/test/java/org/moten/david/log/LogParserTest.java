package org.moten.david.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import com.google.common.io.LineReader;

public class LogParserTest {

	@Test
	public void testParseLine() {
		String line = "2012-11-29 04:39:02.941   INFO  au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor - fixes queue size = 0";
		LogParser p = new LogParser();
		LogEntry entry = p.parse(line);
		assertNotNull(entry);
		assertEquals("INFO", entry.getProperties().get("logLevel"));
		assertEquals("au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor",
				entry.getProperties().get("logLogger"));
		assertEquals("fixes queue size = 0", entry.getProperties()
				.get("logMsg"));
	}

	@Test
	public void testParseNullLineReturnsNull() {
		LogParser p = new LogParser();
		LogEntry entry = p.parse(null);
		assertNull(entry);
	}

	@Test
	public void testParseMultipleLines() throws IOException {
		LogParser p = new LogParser();
		LineReader reader = new LineReader(new InputStreamReader(
				LogParserTest.class.getResourceAsStream("/test.log")));
		String line;
		MessageSplitter splitter = new MessageSplitter();
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			LogEntry entry = p.parse(line);
			if (entry != null) {
				System.out.println(entry);
				System.out.println(splitter.split(entry.getProperties().get(
						LogParser.FIELD_MSG)));
			}
		}
	}
}
