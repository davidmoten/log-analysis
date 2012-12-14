package org.moten.david.log.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.moten.david.log.core.Database;
import org.moten.david.log.core.LogFile;
import org.moten.david.log.core.LogParser;

public class LogFileTest {

	@Test
	public void testTailingAFilePutsRecordsIntoDatabase()
			throws InterruptedException {
		Database db = new Database(new File("target/test3"));
		assertEquals(0, db.getNumEntries());
		LogFile log = new LogFile(new File("src/test/resources/test.log"), 300,
				new LogParser(), Executors.newFixedThreadPool(3));
		log.tail(db);
		Thread.sleep(1000);
		log.stop();
		long numEntries = db.getNumEntries();
		System.out.println(numEntries);
		assertTrue(numEntries > 0);
		db.close();
	}
}
