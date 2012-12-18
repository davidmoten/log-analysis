package org.moten.david.log.server;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.moten.david.log.core.Database;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class Server {

	public void start(boolean persistDummyRecords) {
		try {
			LogManager
					.getLogManager()
					.readConfiguration(
							ServerMain.class
									.getResourceAsStream("/orientdb-server-log.properties"));
			System.setProperty("orientdb.www.path",
					System.getProperty("user.dir") + "/src/main/webapp");

			OServer server = OServerMain.create();
			server.startup(ServerMain.class
					.getResourceAsStream("/orientdb-server-config.xml"));
			server.activate();

			if (persistDummyRecords) {
				Database database = new Database("remote:localhost/logs",
						"admin", "admin");
				database.persistDummyRecords();
			}
			Logger.getLogger(Server.class.getName()).info("started");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
