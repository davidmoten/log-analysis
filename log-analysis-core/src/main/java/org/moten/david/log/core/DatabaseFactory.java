package org.moten.david.log.core;

import java.io.File;

public class DatabaseFactory {

	private final String url;
	private final String username;
	private final String password;
	private final File file;
	private Database fileDb;

	public DatabaseFactory(String url, String username, String password) {
		this(url, username, password, null);
	}

	public DatabaseFactory(File file) {
		this(null, null, null, file);
	}

	private DatabaseFactory(String url, String username, String password,
			File file) {
		this.url = url;
		this.username = username;
		this.password = password;
		this.file = file;
	}

	private synchronized Database getFileDb() {
		if (fileDb == null)
			fileDb = new Database(file);
		return fileDb;
	}

	public Database create() {
		if (file == null)
			return new Database(url, username, password);
		else
			return getFileDb();
	}

}
