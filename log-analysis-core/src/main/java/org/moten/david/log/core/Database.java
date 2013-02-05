package org.moten.david.log.core;

import java.util.Set;

import org.moten.david.log.query.BucketQuery;
import org.moten.david.log.query.Buckets;

public interface Database {

	public static final String TABLE_ENTRY = "Entry";

	/**
	 * Closes the connection to the database returns a new instance of
	 * {@link DatabaseOrient}.
	 * 
	 * @return
	 */
	public abstract Database reconnect();

	/**
	 * Indicate to orientdb that database is being used from another thread
	 * instead.
	 */
	public abstract void useInCurrentThread();

	public abstract void persist(LogEntry entry);

	/**
	 * Return the result of an aggregated/non-aggregated query.
	 * 
	 * @param query
	 * @return
	 */
	public abstract Buckets execute(BucketQuery query);

	/**
	 * Returns the current size of the database in bytes.
	 * 
	 * @return
	 */
	public abstract long size();

	public abstract long getNumEntries();

	/**
	 * Closes the database connection.
	 */
	public abstract void close();

	public abstract Set<String> getKeys();

	/**
	 * Persists n random values in the range with times randomly selected.
	 * 
	 * @param n
	 * */
	public abstract void persistDummyRecords(long n);

	public abstract Iterable<String> getLogs(long startTime, long finishTime);

}