package org.moten.david.log.persister;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.moten.david.log.persister.config.Configuration;
import org.moten.david.log.persister.config.Marshaller;

public class ConfigurationLoadTest {

	@Test
	public void testLoadOfSampleConfiguration() {
		Configuration configuration = new Marshaller()
				.unmarshal(ConfigurationLoadTest.class
						.getResourceAsStream("/sample-persister-configuration.xml"));
		assertEquals("UTC", configuration.parser.timezone);
	}
}
