package org.moten.david.log.configuration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.moten.david.log.core.LogParserOptions;

public class ConfigurationTest {

	@Test
	public void testMarshall() {
		Marshaller marshaller = new Marshaller();
		Configuration configuration = new Configuration();
		Group group = new Group();
		configuration.group.add(group);
		group.log.add(new Log("/home/dave/logs/app/tomcatlog4j.log\\..*",
				true));
		group.pattern = "^(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d) +(\\S+) +(\\S+) +(\\S+)? ?- (.*)$";
		group.timestampFormat = "yyyy-MM-dd HH:mm:ss.SSS";
		group.patternGroups = "logTimestamp,logLevel,logLogger,threadName,logMsg";
		group.timezone = "UTC";
		group.multiline = false;
		marshaller.marshal(configuration, System.out);
	}

	@Test
	public void testUnmarshall() {
		Marshaller marshaller = new Marshaller();
		Configuration c = marshaller.unmarshal(ConfigurationTest.class
				.getResourceAsStream("/configuration-test.xml"));
		assertEquals("UTC", c.group.get(0).timezone);
	}

	@Test
	public void testLoadLogParserOptions() {
		Marshaller marshaller = new Marshaller();
		Configuration c = marshaller.unmarshal(ConfigurationTest.class
				.getResourceAsStream("/configuration-test.xml"));
		LogParserOptions options = LogParserOptions.load(c.group.get(0));
		assertEquals("UTC", options.getTimezone());
	}
}
