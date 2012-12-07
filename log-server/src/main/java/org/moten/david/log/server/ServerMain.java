package org.moten.david.log.server;

import java.util.logging.LogManager;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class ServerMain {

	public static void main(String[] args) throws Exception {
		LogManager
				.getLogManager()
				.readConfiguration(
						ServerMain.class
								.getResourceAsStream("/orientdb-server-log.properties"));

		OServer server = OServerMain.create();
		server.startup(ServerMain.class
				.getResourceAsStream("/orientdb-server-config.xml"));
		server.activate();
	}

}
