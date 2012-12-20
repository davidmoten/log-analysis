package org.moten.david.log.persister;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Test;
import org.moten.david.log.configuration.Log;
import org.moten.david.log.persister.Util;

import com.google.common.collect.Sets;

public class UtilTest {

	private static final Logger log = Logger
			.getLogger(UtilTest.class.getName());

	@Test
	public void testGetLogsNoWildcards() {
		List<Log> list = Util
				.getLogs(new String[] { "src/test/resources/test.log" });
		assertEquals("src/test/resources/test.log", list.get(0).path);
	}

	@Test
	public void testGetLogsWildcards() {
		List<Log> list = Util
				.getLogs(new String[] { "src/test/resources/test(2|3)\\.log" });
		Set<String> paths = Sets.newHashSet();
		paths.add(list.get(0).path);
		paths.add(list.get(1).path);
		log.info("paths=" + paths);
		assertTrue(paths.contains("src/test/resources/test2.log"));
		assertTrue(paths.contains("src/test/resources/test3.log"));
		assertEquals(2, list.size());
	}

	@Test
	public void testGetPath() {
		assertEquals("/src/test/resources/",
				Util.getPath("/src/test/resources/a\\.[0-9]?\\.log"));
	}

	@Test
	public void testGetFilename() {
		assertEquals("a\\.[0-9]?\\.log",
				Util.getFilename("/src/test/resources/a\\.[0-9]?\\.log"));
	}

	@Test
	public void testParsePath() {
		assertEquals("/ausdev/container/logs/cts/",
				Util.getPath("/ausdev/container/logs/cts/cts.log.*"));
	}

	@Test
	public void testParseFilename() {
		assertEquals("cts.log.*",
				Util.getFilename("/ausdev/container/logs/cts/cts.log.*"));
	}
}
