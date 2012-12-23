package org.moten.david.log.persister.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.moten.david.log.core.LogParserOptions;
import org.moten.david.log.persister.config.Configuration;
import org.moten.david.log.persister.config.Group;
import org.moten.david.log.persister.config.Log;
import org.moten.david.log.persister.config.Marshaller;
import org.moten.david.log.persister.config.Parser;

public class ConfigurationTest {

	private static final String PERSISTER_CONFIGURATION_TEST_XML = "/persister-configuration-test.xml";

	@Test
	public void testMarshall() {
		Marshaller marshaller = new Marshaller();
		Configuration configuration = new Configuration();
		Group group = new Group();
		configuration.group.add(group);
		group.log
				.add(new Log("/home/dave/logs/app/tomcatlog4j.log\\..*", true));
		// TOOD use constructor
		Parser parser = new Parser();
		group.parser = parser;
		parser.pattern = "^(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d) +(\\S+) +(\\S+) +(\\S+)? ?- (.*)$";
		parser.timestampFormat = "yyyy-MM-dd HH:mm:ss.SSS";
		parser.patternGroups = "logTimestamp,logLevel,logLogger,threadName,logMsg";
		parser.timezone = "UTC";
		parser.multiline = false;
		marshaller.marshal(configuration, System.out);
	}

	@Test
	public void testUnmarshall() {
		Marshaller marshaller = new Marshaller();
		Configuration c = marshaller.unmarshal(ConfigurationTest.class
				.getResourceAsStream(PERSISTER_CONFIGURATION_TEST_XML));
		assertEquals("UTC", c.group.get(0).parser.timezone);
	}

	@Test
	public void testLoadLogParserOptions() {
		Marshaller marshaller = new Marshaller();
		Configuration c = marshaller.unmarshal(ConfigurationTest.class
				.getResourceAsStream(PERSISTER_CONFIGURATION_TEST_XML));
		LogParserOptions options = LogParserOptions.load(c.parser,
				c.group.get(0));
		assertEquals("UTC", options.getTimezone());
	}
}
