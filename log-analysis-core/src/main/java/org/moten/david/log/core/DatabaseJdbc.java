package org.moten.david.log.core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.moten.david.log.query.BucketQuery;
import org.moten.david.log.query.Buckets;

public class DatabaseJdbc implements Database {

	private static final Logger log = Logger.getLogger(DatabaseJdbc.class
			.getName());

	private final String url;
	private final String username;
	private final String password;
	private final Connection connection;

	private final PreparedStatement stmtInsertEntry;

	private final PreparedStatement stmtInsertProperty;

	public DatabaseJdbc(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		try {
			connection = DriverManager.getConnection(url, username, password);
			createDatabase(connection);
			connection.setAutoCommit(false);
			stmtInsertEntry = connection
					.prepareStatement("insert into Entry(entry_id, time,text) values(?,?,?)");
			stmtInsertProperty = connection
					.prepareStatement("insert into Property(entry_id,name,numeric_Value,text_Value) values(?,?,?,?)");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public DatabaseJdbc(File file) {
		this("jdbc:h2:" + file.getAbsolutePath(), "", "");
	}

	@Override
	public Database reconnect() {
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		return new DatabaseJdbc(url, username, password);
	}

	public static void createDatabase(Connection con) {

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

	@Override
	public void useInCurrentThread() {
		// does nothing, not required
	}

	@Override
	public void persist(LogEntry entry) {
		try {
			String entryId = UUID.randomUUID().toString();
			stmtInsertEntry.setString(1, entryId);
			stmtInsertEntry.setString(2, Util.getString(entry.getProperties()));
			stmtInsertEntry.setDate(3, new java.sql.Date(entry.getTime()));
			stmtInsertEntry.execute();
			for (Entry<String, String> en : entry.getProperties().entrySet()) {
				stmtInsertProperty.setString(1, entryId);
				stmtInsertProperty.setString(2, en.getKey());
				stmtInsertProperty.setString(3, en.getValue());
				stmtInsertProperty.execute();
			}
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
			throw new RuntimeException(e);
		}

	}

	@Override
	public Buckets execute(BucketQuery query) {
		// log.info(query.toString());
		// OSQLSynchQuery<ODocument> sqlQuery = new OSQLSynchQuery<ODocument>(
		// query.getSql());
		// long t = System.currentTimeMillis();
		// List<ODocument> result = db.query(sqlQuery);
		// log.info("query result returned, queryTimeMs="
		// + (System.currentTimeMillis() - t) + "ms");
		// Buckets buckets = new Buckets(query);
		// int i = 0;
		// for (ODocument doc : result) {
		// i++;
		// if (i % 10000 == 0)
		// log.info(i + " records");
		// Long timestamp = doc.field(Field.TIMESTAMP);
		// if (doc.field(Field.VALUE) != null) {
		// try {
		// Object o = doc.field(Field.VALUE);
		// double value;
		// if (o instanceof Number) {
		// value = ((Number) o).doubleValue();
		// } else
		// value = Double.parseDouble(o.toString());
		// buckets.add(timestamp, value);
		// } catch (NumberFormatException e) {
		// // not a number don't care about it
		// }
		// }
		// }
		// log.info("found " + result.size() + " records");
		// return buckets;
		return null;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumEntries() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persistDummyRecords(long n) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterable<String> getLogs(long startTime, long finishTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
