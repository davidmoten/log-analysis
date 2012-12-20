package org.moten.david.log.persister;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.moten.david.log.configuration.Configuration;
import org.moten.david.log.configuration.Marshaller;
import org.moten.david.log.core.Database;
import org.moten.david.log.core.DatabaseFactory;

public class Main {

	private static final String DEFAULT_CONFIGURATION_LOCATION = "/log-analysis-configuration.xml";
	private static Logger log = Logger.getLogger(Main.class.getName());

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

		Configuration configuration = getConfiguration();
		setupLogging();

		String url = System
				.getProperty("logan.db.url", "remote:localhost/logs");
		DatabaseFactory provider = new DatabaseFactory(url, "admin", "admin");
		Database db = provider.create();
		db.persistDummyRecords();
		db.close();
		log.info("loaded dummy records");
		Watcher w = new Watcher(provider, configuration);
		w.start();
	}

	private static void setupLogging() throws IOException {
		LogManager.getLogManager().readConfiguration(
				Main.class.getResourceAsStream("/my-logging.properties"));
	}

	private static Configuration getConfiguration()
			throws FileNotFoundException {
		String configLocation = System.getProperty("logan.config",
				DEFAULT_CONFIGURATION_LOCATION);
		InputStream is = Main.class.getResourceAsStream(configLocation);
		if (is == null) {
			File file = new File(configLocation);
			if (file.exists())
				is = new FileInputStream(configLocation);
			else
				throw new RuntimeException(
						"configuration xml not found. Set property log.analysis.configuration to a file on classpath or filesystem.");
		}
		Configuration configuration = new Marshaller().unmarshal(is);
		return configuration;
	}
}
