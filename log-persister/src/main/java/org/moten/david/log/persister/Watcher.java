package org.moten.david.log.persister;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.moten.david.log.core.DatabaseFactory;
import org.moten.david.log.core.LogFile;
import org.moten.david.log.core.LogParser;
import org.moten.david.log.core.LogParserOptions;
import org.moten.david.log.persister.config.Configuration;
import org.moten.david.log.persister.config.Group;
import org.moten.david.log.persister.config.Log;

import com.google.common.collect.Lists;

/**
 * Watches (tails) groups of files configured by persister configuration and
 * reports lines to the <i>log-database</i>.
 * 
 * @author dave
 * 
 */
public class Watcher {

	private static final int TERMINATION_TIMEOUT_MS = 30000;

	private static final Logger log = Logger.getLogger(Watcher.class.getName());

	private final DatabaseFactory factory;

	private final ExecutorService executor;

	private final List<LogFile> logs = Lists.newArrayList();

	private final Configuration configuration;

	/**
	 * Constructor.
	 * 
	 * @param factory
	 * @param configuration
	 */
	public Watcher(DatabaseFactory factory, Configuration configuration) {
		this.factory = factory;
		this.configuration = configuration;
		executor = Executors.newFixedThreadPool(20);
	}

	/**
	 * Starts tailing threads for each configured matched file.
	 */
	public void start() {
		log.info("starting watcher");
		for (Group group : configuration.group) {
			log.info("starting group " + group);
			for (Log lg : group.log) {
				for (File file : Util
						.getFilesFromPathWithRegexFilename(lg.path)) {
					log.info("starting tail on " + file);
					LogParserOptions options = LogParserOptions.load(
							configuration.parser, group);
					LogFile logFile = new LogFile(file, lg.source, 500,
							new LogParser(options), executor);
					logFile.tail(factory);
					logs.add(logFile);
				}
			}
		}
		log.info("started watcher");
	}

	/**
	 * Stops each thread watching a file and shuts down the executor that
	 * started the threads.
	 */
	public void stop() {
		log.info("stopping watcher");
		for (LogFile lg : logs) {
			lg.stop();
		}
		executor.shutdownNow();
		try {
			executor.awaitTermination(TERMINATION_TIMEOUT_MS,
					TimeUnit.MILLISECONDS);
			log.info("stopped watcher");
		} catch (InterruptedException e) {
			throw new RuntimeException("failed to stop running threads", e);
		}
	}

}
