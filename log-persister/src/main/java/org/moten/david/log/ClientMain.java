package org.moten.david.log;

import java.io.IOException;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.moten.david.log.config.Log;
import org.moten.david.log.config.Options;
import org.moten.david.log.core.Database;
import org.moten.david.log.core.DatabaseFactory;

public class ClientMain {

	private static Logger log = Logger.getLogger(ClientMain.class.getName());

	/**
	 * <p>
	 * -DlogPaths = f1,f2,f3 etc.
	 * </p>
	 * <p>
	 * where f1, etc is of the form:
	 * </p>
	 * <code>/some/path/finished/by/regex</code>
	 * 
	 * @param args
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SecurityException,
			IOException {
		LogManager.getLogManager().readConfiguration(
				ClientMain.class.getResourceAsStream("/my-logging.properties"));
		String name = System.getProperty("logName", "logFile");
		String paths = System.getProperty("logPaths",
				"src/test/resources/test.log");
		String url = System.getProperty("url", "remote:localhost/logs");
		String[] items = paths.split(",");
		log.info("paths=" + paths);
		List<Log> list = Util.getLogs(name, items);
		Options options = new Options(null, null, list);
		DatabaseFactory provider = new DatabaseFactory(url, "admin", "admin");
		Database db = provider.create();
		db.persistDummyRecords();
		db.close();
		log.info("loaded dummy records");
		Watcher w = new Watcher(provider, options);
		w.start();
	}

}
