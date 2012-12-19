package org.moten.david.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;

import org.junit.Test;
import org.moten.david.log.config.Log;
import org.moten.david.log.config.Options;
import org.moten.david.log.core.DatabaseFactory;
import org.moten.david.log.core.LogParser;

import com.google.common.collect.Lists;

public class WatcherTest {

	private static final String TEST_LOG = "target/test.log";

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws SecurityException
	 */
	@Test
	public void test() throws InterruptedException, SecurityException,
			IOException {
		LogManager.getLogManager()
				.readConfiguration(
						WatcherTest.class
								.getResourceAsStream("/my-logging.properties"));

		List<Log> list = Lists.newArrayList();
		list.add(new Log("dummy", TEST_LOG));
		list.add(new Log("dummy2", TEST_LOG));
		Options options = new Options(null, null, list);
		DatabaseFactory factory = new DatabaseFactory(new File("target/test4"));
		Watcher w = new Watcher(factory, options);
		w.start();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					FileOutputStream fos = new FileOutputStream(TEST_LOG);
					DateFormat df = new SimpleDateFormat(LogParser.DATE_FORMAT_DEFAULT);
					for (int i = 1; i <= 5; i++) {
						String line = df.format(new Date())
								+ " INFO org.moten.david.log.something - value="
								+ (System.currentTimeMillis() % 20) + "\n";
						fos.write(line.getBytes());
						fos.flush();
						Thread.sleep(100);
					}
					fos.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
		t.start();
		Thread.sleep(3000);
		w.stop();
	}
}
