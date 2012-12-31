package org.moten.david.log.persister;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Test;

import com.google.common.collect.Sets;

public class UtilTest {

	private static final Logger log = Logger
			.getLogger(UtilTest.class.getName());

	@Test
	public void testGetLogsNoWildcards() {

		List<File> files = Util
				.getFilesFromPathWithRegexFilename("src/test/resources/test.log");
		assertEquals("test.log", files.get(0).getName());
		assertEquals(1, files.size());
	}

	@Test
	public void testGetMatchingFiles() {
		List<File> files = Util
				.getFilesFromPathWithRegexFilename("src/test/resources/test(2|3)\\.log");
		Set<String> paths = Sets.newHashSet();
		paths.add(files.get(0).getName());
		paths.add(files.get(1).getName());
		log.info("paths=" + paths);
		assertTrue(paths.contains("test2.log"));
		assertTrue(paths.contains("test3.log"));
		assertEquals(2, files.size());
	}

	@Test
	public void testGetPath() {
		assertEquals("/src/test/resources/",
				Util.getDirectory("/src/test/resources/a\\.[0-9]?\\.log"));
	}

	@Test
	public void testGetFilename() {
		assertEquals("a\\.[0-9]?\\.log",
				Util.getFilename("/src/test/resources/a\\.[0-9]?\\.log"));
	}

	@Test
	public void testParsePath() {
		assertEquals("/ausdev/container/logs/cts/",
				Util.getDirectory("/ausdev/container/logs/cts/cts.log.*"));
	}

	@Test
	public void testParseFilename() {
		assertEquals("cts.log.*",
				Util.getFilename("/ausdev/container/logs/cts/cts.log.*"));
	}
}
