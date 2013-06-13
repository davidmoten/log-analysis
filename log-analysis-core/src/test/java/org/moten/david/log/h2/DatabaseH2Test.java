package org.moten.david.log.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

public class DatabaseH2Test {

	@Test
	public void test() throws SQLException {
		Connection con = createConnection();
		createDatabase(con);
		con.close();
	}

	private static Connection createConnection() {
		try {
			return DriverManager.getConnection("jdbc:h2:mem:");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static void createDatabase(Connection con) {

		execute(con,
				"create table if not exists entry( entry_id varchar2(255) primary key, time timestamp not null,text varchar2(4000) not null)");
		execute(con,
				"create table if not exists property("
						+ " entry_id varchar2(255) not null,"
						+ " name varchar2(255) not null,"
						+ " numeric_value double,"
						+ " text_value varchar2(1000)"
						+ ", primary key (entry_id, name) "
						+ ", constraint fk_property_entry_id foreign key (entry_id) references entry(entry_id) "
						+ ")");
	}

	private static void execute(Connection con, String sql) {
		try {
			con.prepareStatement(sql).execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
