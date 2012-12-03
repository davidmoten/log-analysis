package org.moten.david.log;

import org.junit.Test;
import org.moten.david.log.query.Sql;

public class SqlTest {

	@Test
	public void parseSqlTest() {
		Sql sql = new Sql("select a from b where c > d");
		System.out.println(sql);
	}

}
