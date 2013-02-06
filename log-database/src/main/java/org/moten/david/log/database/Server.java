package org.moten.david.log.database;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class Server {

	public void start() {
		try {
			org.h2.tools.Server.main(new String[] { "-web", "-webPort", "9000",
					"-tcp", "-baseDir", "target" });
			Connection con = DriverManager.getConnection(
					"jdbc:h2:tcp://localhost/target/test", "", "");
			try {
				con.createStatement().execute("drop table Entry");
			} catch (SQLException e) {
				System.out.println(e.getMessage());

			}

			List<String> lines = IOUtils.readLines(
					Server.class.getResourceAsStream("/h2-create-script.sql"),
					Charset.forName("UTF-8"));
			for (String line : lines) {
				con.createStatement().execute(line);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		new Server().start();
	}

}
