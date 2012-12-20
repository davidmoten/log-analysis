package org.moten.david.log.configuration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigurationTest {

	@Test
	public void testMarshall() {
		Marshaller marshaller = new Marshaller();
		Configuration configuration = new Configuration();
		Group group = new Group();
		configuration.group.add(group);
		group.path.add("/home/dave/logs/app/tomcatlog4j.log\\..*");
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
}
