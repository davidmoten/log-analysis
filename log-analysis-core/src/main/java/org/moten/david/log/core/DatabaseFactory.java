package org.moten.david.log.core;

import java.io.File;

/**
 * Factory for creating instances of {@link DatabaseOrient}.
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
	private Database remoteDb;

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
			fileDb = new DatabaseOrient(file);
		return fileDb;
	}

	/**
	 * Return singleton instance of remote Database.
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	private synchronized Database getRemoteDb(String url, String username,
			String password) {
		if (remoteDb == null)
			remoteDb = new DatabaseOrient(url, username, password);
		return remoteDb;
	}

	/**
	 * Creates an instance of the logs database including fields and indexes.
	 * 
	 * @return
	 */
	public Database create() {
		if (file == null)
			return getRemoteDb(url, username, password);
		else
			return getFileDb();
	}

}
