package org.moten.david.log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class LogFile {

	private final File file;
	private final long checkIntervalMs;
	private Tailer tailer;
	private final LogParser parser;

	public LogFile(File file, long checkIntervalMs, LogParser parser) {
		this.file = file;
		this.checkIntervalMs = checkIntervalMs;
		this.parser = parser;
	}

	public void tail(Database db) {

		TailerListener listener = createListener(db);
		// tail from the start of the file
		tailer = new Tailer(file, listener, checkIntervalMs, false);

		ExecutorService executor = Executors.newFixedThreadPool(5);
		executor.execute(tailer);
	}

	public void stop() {
		if (tailer != null)
			tailer.stop();
	}

	private TailerListener createListener(final Database db) {
		return new TailerListener() {

			@Override
			public void fileNotFound() {
				System.out.println("file not found");
			}

			@Override
			public void fileRotated() {
				System.out.println("file rotated");

			}

			@Override
			public void handle(String line) {
				db.useInCurrentThread();
				LogEntry entry = parser.parse(line);
				if (entry != null)
					db.persist(entry);
			}

			@Override
			public void handle(Exception arg0) {
				System.out.println("handle exception " + arg0);
			}

			@Override
			public void init(Tailer tailer) {
				System.out.println("init");
			}
		};
	}

}
