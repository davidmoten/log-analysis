package org.moten.david.log.core;

import java.io.File;

/**
 * Factory for creating instances of {@link Database}.
 * 
 * @author dave
 * 
 */
public class DatabaseFactory {

	private final String url;
	private final String username;
	private final String password;
	private final File file;
	private Database fileDb;

	/**
	 * Constructor.
	 * 
	 * @param url
	 * @param username
	 * @param password
	 */
	public DatabaseFactory(String url, String username, String password) {
		this(url, username, password, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param file
	 */
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

	/**
	 * Creates an instance of the logs database including fields and indexes.
	 * 
	 * @return
	 */
	public Database create() {
		if (file == null)
			return new Database(url, username, password);
		else
			return getFileDb();
	}

}
