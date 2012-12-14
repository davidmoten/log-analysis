package org.moten.david.log.server;

import java.io.File;

public class ServerMain {

	public static void main(String[] args) throws Exception {
		boolean persistDummyRecords = "true".equalsIgnoreCase(System
				.getProperty("persist.dummy"));
		new Server().start(persistDummyRecords);
		new File("target/db-started").createNewFile();
	}
}
