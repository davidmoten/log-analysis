package org.moten.david.log;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class MessageSplitterTest {

	@Test
	public void testReturnsNullGivenNull() {
		MessageSplitter m = new MessageSplitter();
		assertEquals(0, m.split(null).size());
	}

	@Test
	public void testReturnsNullGivenBlank() {
		MessageSplitter m = new MessageSplitter();
		assertEquals(0, m.split("").size());
	}

	@Test
	public void testReturnsNullGivenTextWithoutEquals() {
		MessageSplitter m = new MessageSplitter();
		assertEquals(0, m.split("abc def").size());
	}

	@Test
	public void testReturnsMapGivenEqualsStatement() {
		MessageSplitter m = new MessageSplitter();
		Map<String, String> map = m.split("a=bcd;");
		assertEquals("bcd", map.get("a"));
	}

}
