package org.moten.david.log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class LogFile {

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

	public void tail(Database db) {

		TailerListener listener = createListener(db);
		// tail from the start of the file
		tailer = new Tailer(file, listener, checkIntervalMs, false);

		// start in separate thread
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
				System.out.println(line);
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
