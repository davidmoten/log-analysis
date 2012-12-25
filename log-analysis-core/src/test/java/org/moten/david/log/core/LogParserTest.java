package org.moten.david.log.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.LineReader;

public class LogParserTest {

	@Test
	public void testParseLine() {
		String line = "2012-11-29 04:39:02.941   INFO  au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor - fixes queue size = 0";
		LogParser p = new LogParser();
		LogEntry entry = p.parse(line);
		assertNotNull(entry);
		assertEquals(1354163942941L, entry.getTime());
		assertEquals("INFO", entry.getProperties().get(Field.FIELD_LOG_LEVEL));
		assertEquals("au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor",
				entry.getProperties().get(Field.FIELD_LOGGER));
		assertEquals("fixes queue size = 0",
				entry.getProperties().get(Field.FIELD_MSG));
	}

	@Test
	public void testParseUtilLoggingLines() {
		String line1 = "23/12/2012 6:58:04 AM org.moten.david.log.core.Database persistDummyRecords";
		String line2 = "INFO: persisted random values=1000 from the last hour to table Dummy";
		String line3 = "some junk";
		String line4 = "23/12/2012 7:00:08 AM org.moten.david.log.core.DatabaseThing persistDummyRecordsAgain";
		String line5 = "DEBUG: something=123";

		Pattern pattern = Pattern
				.compile("^(\\d\\d/\\d\\d/\\d\\d\\d\\d \\d\\d?:\\d\\d:\\d\\d (?:(?:AM)|(?:PM))) +(\\S+) +(\\S+)ZZZ(\\S+): (.*)$");
		Pattern messagePattern = Pattern
				.compile(MessageSplitter.MESSAGE_PATTERN_DEFAULT);
		String format = "dd/MM/yyyy hh:mm:ss a";
		BiMap<String, Integer> map = HashBiMap.create(5);
		map.put(Field.FIELD_LOG_TIMESTAMP, 1);
		map.put(Field.FIELD_LOGGER, 2);
		map.put(Field.FIELD_METHOD, 3);
		map.put(Field.FIELD_LOG_LEVEL, 4);
		map.put(Field.FIELD_MSG, 5);

		LogParserOptions options = new LogParserOptions(pattern, map,
				messagePattern, format, "UTC", true);
		LogParser p = new LogParser(options);
		assertNull(p.parse(line1));
		{
			LogEntry entry = p.parse(line2);
			assertNotNull(entry);
			assertEquals(1356245884000L, entry.getTime());
			assertEquals("INFO",
					entry.getProperties().get(Field.FIELD_LOG_LEVEL));
			assertEquals("org.moten.david.log.core.Database", entry
					.getProperties().get(Field.FIELD_LOGGER));
			assertEquals("persistDummyRecords",
					entry.getProperties().get(Field.FIELD_METHOD));
			assertEquals(
					"persisted random values=1000 from the last hour to table Dummy",
					entry.getProperties().get(Field.FIELD_MSG));
		}
		assertNull(p.parse(line3));
		assertNull(p.parse(line4));
		{
			LogEntry entry = p.parse(line5);
			assertNotNull(entry);
			assertEquals(1356246008000L, entry.getTime());
			assertEquals("DEBUG",
					entry.getProperties().get(Field.FIELD_LOG_LEVEL));
			assertEquals("org.moten.david.log.core.DatabaseThing", entry
					.getProperties().get(Field.FIELD_LOGGER));
			assertEquals("persistDummyRecordsAgain",
					entry.getProperties().get(Field.FIELD_METHOD));
			assertEquals("something=123",
					entry.getProperties().get(Field.FIELD_MSG));

		}
	}

	@Test
	public void testParseLineWithThreadName() {
		String line = "2012-11-29 04:39:02.941   INFO  au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor thread_name-1 - fixes queue size = 0";
		LogParser p = new LogParser();
		LogEntry entry = p.parse(line);
		assertNotNull(entry);
		assertEquals("INFO", entry.getProperties().get(Field.FIELD_LOG_LEVEL));
		assertEquals("au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor",
				entry.getProperties().get(Field.FIELD_LOGGER));
		assertEquals("fixes queue size = 0",
				entry.getProperties().get(Field.FIELD_MSG));
		assertEquals("thread_name-1",
				entry.getProperties().get(Field.FIELD_THREAD_NAME));
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
						.get(Field.FIELD_MSG));
				if (map.size() > 0)
					System.out.println(map);
			}
		}
	}
}
