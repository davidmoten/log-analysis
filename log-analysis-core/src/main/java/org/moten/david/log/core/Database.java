package org.moten.david.log.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.moten.david.log.query.BucketQuery;
import org.moten.david.log.query.Buckets;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.storage.OStorage;

//TODO setup for concurrency, use Filter as per https://github.com/nuvolabase/orientdb/wiki/Java-Web-Apps?
public class Database {

	private static final Logger log = Logger
			.getLogger(Database.class.getName());

	private static final String TABLE_ENTRY = "Entry";

	private static final String TABLE_DUMMY = "Dummy";

	private static final String FIELD_LOG_ID = "logId";

	private static final String FIELD_KEY = "logKey";

	private static final String FIELD_VALUE = "logValue";

	private final MessageSplitter splitter = new MessageSplitter();

	private final ODatabaseDocumentTx db;

	private final String url;

	private final String username;

	private final String password;

	public Database(File location) {
		this(connectToDatabase(location), null, null, null);
	}

	public Database(String url, String username, String password) {
		this(connectToDatabase(url, username, password), url, username,
				password);
	}

	private synchronized static ODatabaseDocumentTx connectToDatabase(
			String url, String username, String password) {
		ODatabaseDocumentTx db = ODatabaseDocumentPool.global().acquire(url,
				username, password);
		log.info("obtained db for " + url);
		return db;
	}

	public Database reconnect() {
		close();
		return new Database(url, username, password);
	}

	public Database(ODatabaseDocumentTx db, String url, String username,
			String password) {
		this.db = db;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	private static ODatabaseDocumentTx connectToDatabase(File location) {
		OGlobalConfiguration.STORAGE_KEEP_OPEN.setValue(true);
		OGlobalConfiguration.MVRBTREE_NODE_PAGE_SIZE.setValue(2048);
		OGlobalConfiguration.TX_USE_LOG.setValue(false);
		OGlobalConfiguration.TX_COMMIT_SYNCH.setValue(true);
		OGlobalConfiguration.ENVIRONMENT_CONCURRENT.setValue(true);
		// OGlobalConfiguration.MVRBTREE_LAZY_UPDATES.setValue(-1);
		OGlobalConfiguration.FILE_MMAP_STRATEGY.setValue(1);
		try {
			FileUtils.deleteDirectory(location);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String url = "local:" + getPath(location);
		System.out.println(url);
		ODatabaseDocumentTx db = new ODatabaseDocumentTx(url).create();
		return db;
	}

	/**
	 * Setup indexes.
	 */
	public void configureDatabase() {
		configureDatabase(db);
	}

	private static void configureDatabase(ODatabaseDocumentTx db) {
		try {
			OSchema schema = db.getMetadata().getSchema();
			OClass entry = schema.createClass(TABLE_ENTRY,
					db.addCluster(TABLE_ENTRY, OStorage.CLUSTER_TYPE.PHYSICAL));
			entry.createProperty(LogParser.FIELD_LOG_TIMESTAMP, OType.LONG)
					.setMandatory(true);

			entry.createIndex("LogTimestampIndex", OClass.INDEX_TYPE.NOTUNIQUE,
					LogParser.FIELD_LOG_TIMESTAMP);
			OClass dummy = schema.createClass(TABLE_DUMMY,
					db.addCluster(TABLE_DUMMY, OStorage.CLUSTER_TYPE.PHYSICAL));

			db.getMetadata().getSchema().save();

			db.commit();
		} catch (RuntimeException e) {
			log.log(Level.WARNING, e.getMessage());
			throw e;
		}
	}

	private static String getPath(File location) {
		try {
			return location.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void useInCurrentThread() {
		ODatabaseRecordThreadLocal.INSTANCE.set(db);
	}

	public void persist(LogEntry entry) {
		// create a new document (row in table)
		// persist the full message, timestamp, level logger and threadName
		long timestamp = entry.getTime();
		String id = UUID.randomUUID().toString();

		for (Entry<String, String> e : entry.getProperties().entrySet()) {
			if (e.getValue() != null) {
				persistDocument(timestamp, id, e.getKey(), e.getValue());
			}
		}

		// persist the split fields from the full message
		Map<String, String> map = splitter.split(entry.getMessage());
		if (map.size() > 0)
			log.info(map.toString());
		for (Entry<String, String> e : map.entrySet()) {
			if (e.getValue() != null) {
				ValueAndType v = parse(e.getValue());
				// replace spaces in field names with underscores
				persistDocument(timestamp, id, e.getKey().replace(" ", "_"),
						v.value, v.type);
			}
		}
		db.commit();
	}

	private void persistDocument(long timestamp, String id, String key,
			Object value, OType type) {
		persistDocument(TABLE_ENTRY, timestamp, id, key, value, type);
	}

	private void persistDocument(String table, long timestamp, String id,
			String key, Object value, OType type) {
		ODocument d = new ODocument(table);
		d.field(LogParser.FIELD_LOG_TIMESTAMP, timestamp);
		d.field(FIELD_LOG_ID, id);
		d.field(FIELD_KEY, key);
		d.field(FIELD_VALUE, value, type);
		d.save();

	}

	private void persistDocument(long timestamp, String id, String key,
			String value) {
		persistDocument(timestamp, id, key, value, OType.STRING);
	}

	private static class ValueAndType {
		Object value;
		OType type;

		public ValueAndType(Object value, OType type) {
			super();
			this.value = value;
			this.type = type;
		}
	}

	private ValueAndType parse(String s) {
		// try matching against Integer
		try {
			Integer val = Integer.parseInt(s);
			return new ValueAndType(val, OType.INTEGER);
		} catch (NumberFormatException e) {
			// continue
		}
		// try matching against Double
		try {
			Double val = Double.parseDouble(s);
			return new ValueAndType(val, OType.DOUBLE);
		} catch (NumberFormatException e) {
			// continue
		}
		// try matching against boolean
		if (s.equalsIgnoreCase("true"))
			return new ValueAndType(Boolean.TRUE, OType.BOOLEAN);
		else if (s.equalsIgnoreCase("false"))
			return new ValueAndType(Boolean.FALSE, OType.BOOLEAN);

		return new ValueAndType(s, OType.STRING);
	}

	public Buckets execute(BucketQuery query) {
		log.info(query.toString());
		OSQLSynchQuery<ODocument> sqlQuery = new OSQLSynchQuery<ODocument>(
				query.getSql());
		List<ODocument> result = db.query(sqlQuery);
		Buckets buckets = new Buckets(query);
		for (ODocument doc : result) {
			Long timestamp = doc.field(LogParser.FIELD_LOG_TIMESTAMP);
			if (doc.field(FIELD_VALUE) != null) {
				try {
					double value = Double.parseDouble((String) doc
							.field(FIELD_VALUE));
					buckets.add(timestamp, value);
				} catch (NumberFormatException e) {
					// not a number don't care about it
				}
			}
		}
		log.info("found " + result.size() + " records");
		return buckets;
	}

	public long size() {

		return db.getSize();
	}

	public long getNumEntries() {
		return db.countClass(TABLE_ENTRY);
	}

	public void close() {
		db.close();
	}

	/**
	 * Persists 1000 random values in the range with times randomly selected
	 * from the the last hour.
	 */
	public void persistDummyRecords() {
		long t = System.currentTimeMillis();
		Random r = new Random();
		int n = 1000;
		for (int i = 0; i < n; i++) {
			long time = t - TimeUnit.HOURS.toMillis(1)
					+ r.nextInt((int) TimeUnit.HOURS.toMillis(2));
			String id = UUID.randomUUID().toString();
			int specialNumber = i % (r.nextInt(100) + 1);
			persistDocument(TABLE_DUMMY, time, id, LogParser.FIELD_MSG,
					"specialNumber=" + specialNumber, OType.INTEGER);
			persistDocument(TABLE_DUMMY, time, id, "specialNumber",
					specialNumber + "", OType.STRING);
		}
		db.commit();
		log.info("persisted " + n
				+ " random values from the last hour to table " + TABLE_DUMMY);
	}
}
