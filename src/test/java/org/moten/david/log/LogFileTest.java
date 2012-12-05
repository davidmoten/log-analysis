package org.moten.david.log;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class LogFileTest {

	@Test
	public void testTailingAFilePutsRecordsIntoDatabase()
			throws InterruptedException {
		Database db = new Database("test3");
		long initialSize = db.size();
		LogFile log = new LogFile(new File("src/test/resources/test.log"), 300,
				new LogParser());
		log.tail(db);
		Thread.sleep(1000);
		log.stop();
		assertTrue(db.size() > initialSize);
		db.close();
	}
}
