package org.moten.david.log.database;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.moten.david.log.core.Database;
import org.moten.david.log.core.DatabaseJdbc;

public class CreateDatabaseTest {

	@Test
	public void testCreateSoRemoteDatabaseWillLoadProperly() {
		if (true)
			return;

		File orient = null;
		for (File file : new File("target").listFiles()) {
			if (file.getName().startsWith("orientdb-"))
				orient = file;
		}
		assertNotNull("could not find orient home in target", orient);
		File dbDirectory = new File(orient, "databases/logs");
		try {
			FileUtils.deleteDirectory(dbDirectory);
		} catch (IOException e) {
			// do nothing
		}
		Database db = new DatabaseJdbc(dbDirectory);
		db.close();
	}

	@Test
	public void testCreateH2() throws SQLException, InterruptedException {

	}
}
