package org.moten.david.log.core;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Test;

public class LogFormatterTest {

	@Test
	public void testLogFormatter() throws SecurityException, IOException {
		LogManager.getLogManager().readConfiguration(
				LogFormatterTest.class
						.getResourceAsStream("/my-logging.properties"));

		Logger.getLogger("test").info("hello there");
	}
}
