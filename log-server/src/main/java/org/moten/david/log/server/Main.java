package org.moten.david.log.server;

import java.util.logging.LogManager;

import com.orientechnologies.orient.server.OServer;

public class Main {

	public static void main(String[] args) throws Exception {
		LogManager
				.getLogManager()
				.readConfiguration(
						Main.class
								.getResourceAsStream("/orientdb-server-log.properties"));
		OServer server = new OServer();
		server.startup(Main.class
				.getResourceAsStream("/orientdb-server-config.xml"));
		server.activate();
	}

}
