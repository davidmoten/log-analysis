package org.moten.david.log;

import java.io.IOException;
import java.util.List;
import java.util.logging.LogManager;

import org.moten.david.log.config.Log;
import org.moten.david.log.config.Options;

import com.google.common.collect.Lists;

public class ClientMain {

	public static void main(String[] args) throws SecurityException,
			IOException {
		LogManager.getLogManager().readConfiguration(
				LogManager.class.getResourceAsStream("/my-logging.properties"));
		List<Log> list = Lists.newArrayList();
		String name = System.getProperty("logName", "logFile");
		String path = System.getProperty("logPath",
				"src/test/resources/test.log");
		list.add(new Log(name, path));
		Options options = new Options(null, null, list);
		Database db = new Database("remote:localhost/logs", "admin", "admin");
		Watcher w = new Watcher(db, options);
		w.start();
	}
}
