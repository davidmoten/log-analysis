package org.moten.david.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

public class MessageSplitterTest {

	@Test
	public void testReturnsNullGivenNull() {
		MessageSplitter m = new MessageSplitter();
		assertNull(m.split(null));
	}

	@Test
	public void testReturnsNullGivenBlank() {
		MessageSplitter m = new MessageSplitter();
		assertNull(m.split(""));
	}

	@Test
	public void testReturnsNullGivenTextWithoutEquals() {
		MessageSplitter m = new MessageSplitter();
		assertNull(m.split("abc def"));
	}

	@Test
	public void testReturnsMapGivenEqualsStatement() {
		MessageSplitter m = new MessageSplitter();
		Map<String, String> map = m.split("a=bcd;");
		assertEquals("bcd", map.get("a"));
	}

}
