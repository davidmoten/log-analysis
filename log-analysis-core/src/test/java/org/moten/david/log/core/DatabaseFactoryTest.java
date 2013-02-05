package org.moten.david.log.core;

import java.io.File;

import org.junit.Test;

public class DatabaseFactoryTest {

	@Test
	public void testCreate() {

		String path = "target/database-factory-test";
		new DatabaseOrient(new File(path)).close();
		DatabaseFactory f = new DatabaseFactory("local:" + path, "admin",
				"admin");
		f.create().close();
	}
}
