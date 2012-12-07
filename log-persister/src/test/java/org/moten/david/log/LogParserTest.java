package org.moten.david.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

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
	public void testParseLineWithThreadName() {
		String line = "2012-11-29 04:39:02.941   INFO  au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor thread_name-1 - fixes queue size = 0";
		LogParser p = new LogParser();
		LogEntry entry = p.parse(line);
		assertNotNull(entry);
		assertEquals("INFO",
				entry.getProperties().get(LogParser.FIELD_LOG_LEVEL));
		assertEquals("au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor",
				entry.getProperties().get(LogParser.FIELD_LOGGER));
		assertEquals("fixes queue size = 0",
				entry.getProperties().get(LogParser.FIELD_MSG));
		assertEquals("thread_name-1",
				entry.getProperties().get(LogParser.FIELD_THREAD_NAME));
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
				Map<String, String> map = splitter.split(entry.getProperties()
						.get(LogParser.FIELD_MSG));
				if (map.size() > 0)
					System.out.println(map);
			}
		}
	}
}
