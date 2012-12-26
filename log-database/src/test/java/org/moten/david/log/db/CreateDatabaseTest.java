package org.moten.david.log.db;

import java.io.File;

import org.junit.Test;
import org.moten.david.log.core.Database;

public class CreateDatabaseTest {

	@Test
	public void createLogsDatabase() {
		File file = new File("target/orientdb-1.3.0/databases/logs");
		// ODatabase db = new ODatabaseDocumentTx("local:"
		// + file.getAbsolutePath()).create();
		// db.close();
		Database d = new Database(file);
		d.configureDatabase();
		// d.persistDummyRecords();

		d.close();

	}
}
