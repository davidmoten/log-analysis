package org.moten.david.log;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.moten.david.log.config.Log;
import org.moten.david.log.config.Options;
import org.moten.david.log.core.DatabaseFactory;
import org.moten.david.log.core.LogFile;
import org.moten.david.log.core.LogParser;

import com.google.common.collect.Lists;

public class Watcher {

	private static final int TERMINATION_TIMEOUT_MS = 30000;

	private static final Logger log = Logger.getLogger(Watcher.class.getName());

	private final Options options;

	private final DatabaseFactory factory;

	private final ExecutorService executor;

	private final List<LogFile> logs = Lists.newArrayList();

	public Watcher(DatabaseFactory factory, Options options) {
		this.factory = factory;
		this.options = options;
		executor = Executors.newFixedThreadPool(20);
	}

	public void start() {
		log.info("starting watcher");
		for (Log f : options.getLog()) {
			log.info("starting tail on " + f);
			LogFile logFile = new LogFile(new File(f.getPath()), 500,
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
