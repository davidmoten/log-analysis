package org.moten.david.log.core;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import com.google.common.annotations.VisibleForTesting;

/**
 * Starts a thread using a given {@link ExecutorService} to load all logs from a
 * {@link File} and then monitor the file for new lines and load them too as
 * they arrive.
 * 
 * @author dave
 * 
 */
public class LogFile {

	private static Logger log = Logger.getLogger(LogFile.class.getName());

	private static AtomicLong counter = new AtomicLong();

	private final File file;
	private final long checkIntervalMs;
	private Tailer tailer;
	private final LogParser parser;
	private final ExecutorService executor;

	private final String source;

	/**
	 * Constructor.
	 * 
	 * @param file
	 * @param checkIntervalMs
	 * @param parser
	 * @param executor
	 */
	public LogFile(File file, String source, long checkIntervalMs,
			LogParser parser, ExecutorService executor) {
		this.file = file;
		this.source = source;
		this.checkIntervalMs = checkIntervalMs;
		this.parser = parser;
		this.executor = executor;
		createFileIfDoesntExist(file);
	}

	@VisibleForTesting
	static void createFileIfDoesntExist(File file) {
		if (!file.exists())
			try {
				if (!file.createNewFile())
					throw new RuntimeException("could not create file: " + file);
			} catch (IOException e) {
				throw new RuntimeException("could not create file: " + file, e);
			}
	}

	/**
	 * Starts a thread that tails a file from the start and reports extracted
	 * info from the lines to the database.
	 * 
	 * @param factory
	 */
	public void tail(DatabaseFactory factory) {

		TailerListener listener = createListener(factory.create());

		// tail from the start of the file
		tailer = new Tailer(file, listener, checkIntervalMs, false);

		// start in separate thread
		log.info("starting tailer thread");
		executor.execute(tailer);
	}

	/**
	 * Stops the tailer (and thus its thread).
	 */
	public void stop() {
		if (tailer != null)
			tailer.stop();
	}

	private synchronized static void incrementCounter() {
		if (counter.incrementAndGet() % 1000 == 0)
			log.info(counter + " log lines persisted");
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
				log.info(source + ": " + line);
				try {
					db.useInCurrentThread();
					LogEntry entry = parser.parse(source, line);
					if (entry != null) {
						db.persist(entry);
						incrementCounter();
					}
				} catch (Throwable e) {
					log.log(Level.WARNING, e.getMessage(), e);
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
