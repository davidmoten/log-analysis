package org.moten.david.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

public class PersisterTest {

	@Test
	public void testCreateDbAndPersistRecords() {
		Persister p = new Persister("test1");
		long t = 0;
		for (int i = 1; i <= 1000; i++) {
			Map<String, String> map = Maps.newHashMap();
			map.put("size", i % 27 + "");
			LogEntry entry = new LogEntry(t + i, map);
			p.persist(entry);
		}
	}

	@Test
	public void testCreateDbParseAndPersist() {

		final String lineMessage = " INFO  au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor - fixes queue size = 0";
		System.out.println("creating database");
		Persister p = new Persister("test2");
		LogParser parser = new LogParser();
		DateFormat df = new SimpleDateFormat(LogParser.DATE_FORMAT);

		long n1 = 10000;
		long n2 = 10000;
		System.out
				.println("triggering hotspot compilation by sending some messages");
		persistMessages(lineMessage, p, parser, df, n1);
		System.out.println("parsing and persisting records");
		long timer = System.currentTimeMillis();
		persistMessages(lineMessage, p, parser, df, n2);
		long ms = System.currentTimeMillis() - timer;
		System.out.println("done in " + ms + "ms");
		System.out.println("rate=" + (1000 * n2 / ms) + " lines/s");
	}

	private void persistMessages(final String lineMessage, Persister p,
			LogParser parser, DateFormat df, long n) {
		for (int i = 1; i <= n; i++) {
			long t = System.currentTimeMillis() + i;
			LogEntry entry = parser.parse(df.format(new Date(t)) + lineMessage);
			p.persist(entry);
		}
	}
}
