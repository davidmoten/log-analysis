package org.moten.david.log.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {

	public void start() {
		try {
			org.h2.tools.Server.main(new String[] { "-web", "-webPort", "9000",
					"-tcp" });
			Connection con = DriverManager.getConnection(
					"jdbc:h2:tcp://localhost/~/test", "", "");
			try {
				con.prepareStatement("select * from Entry").execute();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				con.createStatement().execute(
						"create table Entry (id long primary key)");
				con.prepareStatement("select * from Entry").execute();
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		new Server().start();
	}

}
