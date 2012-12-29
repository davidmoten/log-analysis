package org.moten.david.log.persister;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.moten.david.log.core.DatabaseFactory;
import org.moten.david.log.persister.config.Configuration;
import org.moten.david.log.persister.config.Marshaller;

public class Main {

	private static final String DEFAULT_CONFIGURATION_LOCATION = "/persister-configuration.xml";
	private static Logger log = Logger.getLogger(Main.class.getName());

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Configuration configuration = getConfiguration();
		setupLogging();
		DatabaseFactory provider = new DatabaseFactory(
				configuration.databaseUrl, "admin", "admin");
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
						"configuration xml not found. Set property logan.config to a file on classpath or filesystem.");
		}
		Configuration configuration = new Marshaller().unmarshal(is);
		return configuration;
	}
}
