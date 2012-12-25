package org.moten.david.log.orientdb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.moten.david.log.orientdb.SqlSelect;

public class SqlSelectTest {

	@Test
	public void parseSqlTest() {
		SqlSelect sql = new SqlSelect(
				"select a1 , a2 from b where c > d group by e order by f skip 5 limit 10");
		System.out.println(sql);
		assertEquals("a1 , a2", sql.getSelect());
		assertEquals("b", sql.getFrom());
		assertEquals("c > d", sql.getWhere());
		assertEquals("e", sql.getGroupBy());
		assertEquals("f", sql.getOrderBy());
		assertEquals("5", sql.getSkip());
		assertEquals("10", sql.getLimit());
	}

	@Test
	public void testAndMethod() {
		SqlSelect sql = new SqlSelect(
				"select a1 , a2 from b where c > d group by e order by f skip 5 limit 10");

		assertEquals("(c > d) and (g = h)", sql.and("g = h").getWhere());
	}

}
