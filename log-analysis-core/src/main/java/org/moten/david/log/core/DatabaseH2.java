package org.moten.david.log.core;

import java.util.Set;

import org.moten.david.log.query.BucketQuery;
import org.moten.david.log.query.Buckets;

public class DatabaseH2 implements Database {

	public DatabaseH2(String url, String username, String password) {

	}

	@Override
	public Database reconnect() {
		// TODO reconnect properly!
		return this;
	}

	@Override
	public void useInCurrentThread() {
		// does nothing, not required
	}

	@Override
	public void persist(LogEntry entry) {
		// TODO Auto-generated method stub

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
