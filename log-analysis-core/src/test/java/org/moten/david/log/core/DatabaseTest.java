package org.moten.david.log.core;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;
import org.moten.david.log.query.BucketQuery;
import org.moten.david.log.query.Buckets;

import com.google.common.collect.Maps;

public class DatabaseTest {

	@Test
	public void testCreateDbAndPersistRecords() {
		Database p = new Database(new File("target/test1"));
		long t = 0;
		for (int i = 1; i <= 1000; i++) {
			Map<String, String> map = Maps.newHashMap();
			map.put("size", i % 27 + "");
			LogEntry entry = new LogEntry(t + i, map);
			p.persist(entry);
		}
		p.close();
	}

	@Test
	public void testCreateDbParseAndPersist() {

		final String lineMessage = " INFO  au.gov.amsa.er.craft.tracking.actor.FixesPersisterActor - fixes queue size = 0";
		System.out.println("creating database");
		Database p = new Database(new File("target/test2"));
		LogParser parser = new LogParser(LogParserOptions.load());
		DateFormat df = new SimpleDateFormat(LogParser.DATE_FORMAT_DEFAULT);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));

		long n1 = 100;
		long n2 = 100;
		System.out
				.println("triggering hotspot compilation by sending some messages");
		persistMessages(lineMessage, p, parser, df, n1);
		System.out.println("parsing and persisting records");
		long timer = System.currentTimeMillis();

		persistMessages(lineMessage, p, parser, df, n2);

		long ms = System.currentTimeMillis() - timer;
		System.out.println("done in " + ms + "ms");
		System.out.println("rate=" + (1000 * n2 / ms) + " lines/s");
		Buckets r = p.execute(new BucketQuery(new Date(0), 1000, 20, "select "
				+ Database.FIELD_LOG_TIMESTAMP
				+ ", logTimestamp as value from Entry"));
		System.out.println(r);
		p.close();
	}

	private void persistMessages(final String lineMessage, Database p,
			LogParser parser, DateFormat df, long n) {

		for (int i = 0; i < n; i++) {
			long t = i;
			String line = df.format(new Date(t)) + lineMessage;
			LogEntry entry = parser.parse(line);
			p.persist(entry);
		}

	}
}
