package org.moten.david.log.persister;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.moten.david.log.core.Database;
import org.moten.david.log.core.DatabaseFactory;
import org.moten.david.log.persister.config.Configuration;
import org.moten.david.log.persister.config.Marshaller;

/**
 * Reads persister-configuration.xml then starts threads to read/tail log files
 * and report log lines to <i>log-database</i>.
 * 
 * @author dave
 * 
 */
public class Main {

	private static final String DEFAULT_CONFIGURATION_LOCATION = "/persister-configuration.xml";
	private static Logger log = Logger.getLogger(Main.class.getName());

	/**
	 * Main method to start the persister.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Configuration configuration = getConfiguration();
		setupLogging();
		// TODO put username and password into configuration
		DatabaseFactory provider = new DatabaseFactory(
				configuration.connection.url,
				configuration.connection.username,
				configuration.connection.password);
		Database db = provider.create();
		// TODO would prefer not to have to do this!
		db.configureDatabase();
		db.close();
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
