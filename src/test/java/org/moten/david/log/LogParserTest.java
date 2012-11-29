package org.moten.david.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class LogParserTest {

	@Test
	public void test() {
		String line = "2012-11-29 04:39:02.941 INFO  au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor - fixes queue size = 0";
		LogParser p = new LogParser();
		LogEntry entry = p.parse(line);
		assertNotNull(entry);
		assertEquals("INFO", entry.getProperties().get("logLevel"));
		assertEquals("au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor",
				entry.getProperties().get("logLogger"));
		assertEquals("fixes queue size = 0", entry.getProperties()
				.get("logMsg"));
	}
}
