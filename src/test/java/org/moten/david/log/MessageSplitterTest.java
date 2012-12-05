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

	@Test
	public void testReturnsMapGivenTwoEqualsStatementsDelimitedBySemicolon() {
		MessageSplitter m = new MessageSplitter();
		Map<String, String> map = m.split("a=bcd; b=hello there;");
		assertEquals("bcd", map.get("a"));
		assertEquals("hello there", map.get("b"));
	}

	@Test
	public void testReturnsMapGivenTwoEqualsStatementsDelimitedByPipe() {
		MessageSplitter m = new MessageSplitter();
		Map<String, String> map = m.split("a=bcd| b=hello there|");
		assertEquals("bcd", map.get("a"));
		assertEquals("hello there", map.get("b"));
	}

	@Test
	public void testReturnsMapGivenTwoEqualsStatementsDelimitedByPipeValueLeadingAndTrailingSpacesShouldBeIgnored() {
		MessageSplitter m = new MessageSplitter();
		Map<String, String> map = m.split("a= bcd    | b= hello there  |");
		assertEquals("bcd", map.get("a"));
		assertEquals("hello there", map.get("b"));
	}

	@Test
	public void testReturnsMapGivenTwoEqualsStatementsDelimitedByCommaValueLeadingAndTrailingSpacesShouldBeIgnored() {
		MessageSplitter m = new MessageSplitter();
		Map<String, String> map = m.split("a= bcd    , b= hello there  ,");
		assertEquals("bcd", map.get("a"));
		assertEquals("hello there", map.get("b"));
	}

	@Test
	public void testReturnsMapGivenTwoEqualsStatementsDelimitedByCommaValueFinalDelimiterEOL() {
		MessageSplitter m = new MessageSplitter();
		Map<String, String> map = m.split("a= bcd,b=hello there");
		assertEquals("bcd", map.get("a"));
		assertEquals("hello there", map.get("b"));
	}
}
