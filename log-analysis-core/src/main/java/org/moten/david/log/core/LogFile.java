package org.moten.david.log.core;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class LogFile {

	private static Logger log = Logger.getLogger(LogFile.class.getName());

	private final File file;
	private final long checkIntervalMs;
	private Tailer tailer;
	private final LogParser parser;
	private final ExecutorService executor;

	public LogFile(File file, long checkIntervalMs, LogParser parser,
			ExecutorService executor) {
		this.file = file;
		this.checkIntervalMs = checkIntervalMs;
		this.parser = parser;
		this.executor = executor;
		createFileIfDoesntExist(file);
	}

	private void createFileIfDoesntExist(File file) {
		if (!file.exists())
			try {
				if (!file.createNewFile())
					throw new RuntimeException("could not create file: " + file);
			} catch (IOException e) {
				throw new RuntimeException("could not create file: " + file, e);
			}
	}

	public void tail(DatabaseFactory factory) {

		TailerListener listener = createListener(factory.create());
		// tail from the start of the file
		tailer = new Tailer(file, listener, checkIntervalMs, false);

		// start in separate thread
		executor.execute(tailer);
	}

	public void stop() {
		if (tailer != null)
			tailer.stop();
	}

	private TailerListener createListener(final Database dbInitial) {
		return new TailerListener() {
			private Database db = dbInitial;

			@Override
			public void fileNotFound() {
				log.warning("file not found");
			}

			@Override
			public void fileRotated() {
				log.info("file rotated");

			}

			@Override
			public synchronized void handle(String line) {
				try {
					db.useInCurrentThread();
					LogEntry entry = parser.parse(line);
					if (entry != null)
						db.persist(entry);
				} catch (RuntimeException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
					// reconnect
					try {
						db = dbInitial.reconnect();
					} catch (RuntimeException e2) {
						log.info("could not reconnect at this time: "
								+ e2.getMessage());
						log.info("waiting 30s");
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e1) {
							// do nothing
						}
					}
				}
			}

			@Override
			public void handle(Exception e) {
				log.log(Level.WARNING, "handle exception " + e.getMessage(), e);
			}

			@Override
			public void init(Tailer tailer) {
				log.info("init");
			}
		};
	}
}
