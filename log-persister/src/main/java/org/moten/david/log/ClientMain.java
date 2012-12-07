package org.moten.david.log;

import java.io.IOException;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.moten.david.log.config.Log;
import org.moten.david.log.config.Options;

import com.google.common.collect.Lists;

public class ClientMain {

	private static Logger log = Logger.getLogger(ClientMain.class.getName());

	public static void main(String[] args) throws SecurityException,
			IOException {
		LogManager.getLogManager().readConfiguration(
				ClientMain.class.getResourceAsStream("/my-logging.properties"));
		List<Log> list = Lists.newArrayList();
		String name = System.getProperty("logName", "logFile");
		String paths = System.getProperty("logPaths",
				"src/test/resources/test.log");
		String[] items = paths.split(",");
		log.info("paths=" + paths);
		for (String item : items) {
			list.add(new Log(name, item));
			log.info("added " + item);
		}
		Options options = new Options(null, null, list);
		Database db = new Database("remote:localhost/logs", "admin", "admin");
		Watcher w = new Watcher(db, options);
		w.start();
	}
}
