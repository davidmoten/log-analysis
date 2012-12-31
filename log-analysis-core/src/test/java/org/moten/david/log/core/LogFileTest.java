package org.moten.david.log.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.Executors;

import org.junit.Test;

public class LogFileTest {

	@Test
	public void testTailingAFilePutsRecordsIntoDatabase()
			throws InterruptedException {
		DatabaseFactory factory = new DatabaseFactory(new File("target/test3"));
		Database db = factory.create();
		try {
			db.configureDatabase();
			assertEquals(0, db.getNumEntries());
			LogFile log = new LogFile(new File("src/test/resources/test.log"),
					"testing", 300, new LogParser(LogParserOptions.load()),
					Executors.newFixedThreadPool(3));
			log.tail(factory);
			Thread.sleep(1000);
			log.stop();
			long numEntries = db.getNumEntries();
			System.out.println(numEntries);
			assertTrue(numEntries > 0);
		} finally {
			db.close();
		}
	}

	@Test
	public void testCreateIfDoesntExist() {
		File file = new File("target/temp" + System.currentTimeMillis());
		LogFile.createFileIfDoesntExist(file);
		assertTrue(file.exists());
	}

	@Test
	public void testCallingStopWhenNotStartedDoesNotThrowException() {
		LogFile log = new LogFile(new File("src/test/resources/test.log"),
				"testing", 300, new LogParser(LogParserOptions.load()),
				Executors.newFixedThreadPool(3));
		log.stop();

	}
}
