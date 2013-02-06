package org.moten.david.log.core;

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
			connection.setAutoCommit(false);
			stmtInsertEntry = connection
					.prepareStatement("insert into Entry(entry_id,text, entry_time) values(?,?,?)");
			stmtInsertProperty = connection
					.prepareStatement("insert into Property(entry_id,name,val) values(?,?,?)");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
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
		// TODO Auto-generated method stub
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
