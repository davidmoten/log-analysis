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
		System.setProperty("orientdb.www.path", System.getProperty("user.dir")
				+ "/src/main/webapp");

		OServer server = OServerMain.create();
		server.startup(ServerMain.class
				.getResourceAsStream("/orientdb-server-config.xml"));
		server.activate();
	}

}
