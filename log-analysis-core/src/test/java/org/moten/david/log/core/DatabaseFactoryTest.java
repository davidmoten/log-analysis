package org.moten.david.log.core;

import java.io.File;

import org.junit.Test;

public class DatabaseFactoryTest {

	@Test
	public void testCreate() {

		String path = "target/database-factory-test";
		new DatabaseJdbc(new File(path)).close();
		DatabaseFactory f = new DatabaseFactory("local:" + path, "admin",
				"admin");
		f.create().close();
	}
}
