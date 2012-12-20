package org.moten.david.log.persister;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.moten.david.log.configuration.Configuration;
import org.moten.david.log.configuration.Group;
import org.moten.david.log.configuration.Log;
import org.moten.david.log.core.DatabaseFactory;
import org.moten.david.log.core.LogFile;
import org.moten.david.log.core.LogParser;

import com.google.common.collect.Lists;

public class Watcher {

	private static final int TERMINATION_TIMEOUT_MS = 30000;

	private static final Logger log = Logger.getLogger(Watcher.class.getName());

	private final DatabaseFactory factory;

	private final ExecutorService executor;

	private final List<LogFile> logs = Lists.newArrayList();

	private final Configuration configuration;

	public Watcher(DatabaseFactory factory, Configuration configuration) {
		this.factory = factory;
		this.configuration = configuration;
		executor = Executors.newFixedThreadPool(20);
	}

	public void start() {
		log.info("starting watcher");
		Group group = configuration.group.get(0);
		for (Log lg : group.log) {
			log.info("starting tail on " + lg);
			LogFile logFile = new LogFile(new File(lg.path), 500,
					new LogParser(), executor);
			logFile.tail(factory);
			logs.add(logFile);
		}
		log.info("started watcher");
	}

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
