package org.moten.david.log.database;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.moten.david.log.core.Database;

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
		Database db = new Database(dbDirectory);
		db.close();
	}
}
