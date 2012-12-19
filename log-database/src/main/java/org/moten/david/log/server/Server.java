package org.moten.david.log.server;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.moten.david.log.core.Database;
import org.moten.david.log.core.DatabaseFactory;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class Server {

	private static final Logger log = Logger.getLogger(Server.class.getName());

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

			Database database = new DatabaseFactory("remote:localhost/logs",
					"admin", "admin").create();
			database.configureDatabase();
			if (persistDummyRecords) {
				database.persistDummyRecords();
			}
			log.info("started");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
