package org.moten.david.log.core;

import static org.moten.david.log.core.Field.FIELD_LOGGER;
import static org.moten.david.log.core.Field.FIELD_LOG_LEVEL;
import static org.moten.david.log.core.Field.FIELD_LOG_TIMESTAMP;
import static org.moten.david.log.core.Field.FIELD_MSG;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.moten.david.log.query.BucketQuery;
import org.moten.david.log.query.Buckets;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.storage.OStorage;

//TODO setup for concurrency, use Filter as per https://github.com/nuvolabase/orientdb/wiki/Java-Web-Apps?

/**
 * Facade for access to the orient db database either as local or remote
 * instance.
 * 
 * @author dave
 * 
 */
public class Database {

	private static final Logger log = Logger
			.getLogger(Database.class.getName());

	private static final String TABLE_ENTRY = "Entry";

	private static final String TABLE_DUMMY = "Dummy";

	private static final String TABLE_LINE = "Line";

	private final ODatabaseDocumentTx db;

	private final String url;

	private final String username;

	private final String password;

	/**
	 * Constructor.
	 * 
	 * @param location
	 */
	public Database(File location) {
		this(createDatabase(location), null, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param url
	 * @param username
	 * @param password
	 */
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

	/**
	 * Closes the connection to the database returns a new instance of
	 * {@link Database}.
	 * 
	 * @return
	 */
	public Database reconnect() {
		close();
		return new Database(url, username, password);
	}

	/**
	 * Constructor.
	 * 
	 * @param db
	 * @param url
	 * @param username
	 * @param password
	 */
	public Database(ODatabaseDocumentTx db, String url, String username,
			String password) {
		this.db = db;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	/**
	 * Creates the logs database in the filesystem.
	 * 
	 * @param location
	 * @return
	 */
	private static ODatabaseDocumentTx createDatabase(File location) {
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

	/**
	 * Sets up fields and indexes.
	 * 
	 * @param db
	 */
	private static void configureDatabase(ODatabaseDocumentTx db) {
		try {
			OSchema schema = db.getMetadata().getSchema();
			OClass entry = schema.createClass(TABLE_ENTRY,
					db.addCluster(TABLE_ENTRY, OStorage.CLUSTER_TYPE.PHYSICAL));
			entry.createProperty(Field.FIELD_LOG_TIMESTAMP, OType.LONG)
					.setMandatory(true);
			entry.createProperty(Field.FIELD_KEY, OType.STRING).setMandatory(
					true);
			entry.createProperty(Field.FIELD_VALUE, OType.STRING).setMandatory(
					true);
			entry.createProperty(Field.FIELD_LOG_ID, OType.STRING)
					.setMandatory(true);
			entry.createIndex("EntryTimestampIndex",
					OClass.INDEX_TYPE.NOTUNIQUE, Field.FIELD_LOG_TIMESTAMP);
			entry.createIndex("EntryTimestampKeyIndex",
					OClass.INDEX_TYPE.NOTUNIQUE, Field.FIELD_LOG_TIMESTAMP,
					Field.FIELD_KEY);
			entry.createIndex("EntryLogIdIndex", OClass.INDEX_TYPE.NOTUNIQUE,
					Field.FIELD_LOG_ID);

			OClass dummy = schema.createClass(TABLE_DUMMY,
					db.addCluster(TABLE_DUMMY, OStorage.CLUSTER_TYPE.PHYSICAL));
			dummy.createProperty(Field.FIELD_LOG_TIMESTAMP, OType.LONG)
					.setMandatory(true);
			dummy.createIndex("DummyTimestampIndex",
					OClass.INDEX_TYPE.NOTUNIQUE, Field.FIELD_LOG_TIMESTAMP);

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

	/**
	 * Indicate to orientdb that database is being used from another thread
	 * instead.
	 */
	public void useInCurrentThread() {
		ODatabaseRecordThreadLocal.INSTANCE.set(db);
	}

	/**
	 * Persist a log entry to the database.
	 * 
	 * @param entry
	 */
	public void persist(LogEntry entry) {
		// create a new document (row in table)
		// persist the full message, timestamp, level logger and threadName
		long timestamp = entry.getTime();
		String id = UUID.randomUUID().toString();

		ODocument d = new ODocument(TABLE_LINE);
		d.field(Field.FIELD_LOG_TIMESTAMP, timestamp);
		d.field(Field.FIELD_LOG_ID, id);

		Set<ORID> entries = Sets.newHashSet();
		for (Entry<String, String> e : entry.getProperties().entrySet()) {
			if (e.getValue() != null) {
				ValueAndType v = parse(e.getValue());
				ODocument doc = persistDocument(timestamp, id, e.getKey()
						.replace(" ", "_"), v.value, v.type);
				entries.add(doc.getIdentity());
			}
		}

		// TODO create line record that points to the entries
		// d.field(Field.FIELD_ENTRIES, OType.EMBEDDEDSET);

		d.save();
		db.commit();
	}

	private ODocument persistDocument(long timestamp, String id, String key,
			Object value, OType type) {
		return persistDocument(TABLE_ENTRY, timestamp, id, key, value, type);
	}

	private ODocument persistDocument(String table, long timestamp, String id,
			String key, Object value, OType type) {
		ODocument d = new ODocument(table);
		d.field(Field.FIELD_LOG_TIMESTAMP, timestamp);
		d.field(Field.FIELD_LOG_ID, id);
		d.field(Field.FIELD_KEY, key);
		d.field(Field.FIELD_VALUE, value, type);
		d.save();
		return d;
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

	/**
	 * Return the result of an aggregated/non-aggregated query.
	 * 
	 * @param query
	 * @return
	 */
	public Buckets execute(BucketQuery query) {
		log.info(query.toString());
		OSQLSynchQuery<ODocument> sqlQuery = new OSQLSynchQuery<ODocument>(
				query.getSql());
		List<ODocument> result = db.query(sqlQuery);
		Buckets buckets = new Buckets(query);
		for (ODocument doc : result) {
			Long timestamp = doc.field(Field.FIELD_LOG_TIMESTAMP);
			if (doc.field(Field.FIELD_VALUE) != null) {
				try {
					double value = Double.parseDouble((String) doc
							.field(Field.FIELD_VALUE));
					buckets.add(timestamp, value);
				} catch (NumberFormatException e) {
					// not a number don't care about it
				}
			}
		}
		log.info("found " + result.size() + " records");
		return buckets;
	}

	/**
	 * Returns the current size of the database in bytes.
	 * 
	 * @return
	 */
	public long size() {

		return db.getSize();
	}

	public long getNumEntries() {
		return db.countClass(TABLE_ENTRY);
	}

	/**
	 * Closes the database connection.
	 */
	public void close() {
		db.close();
	}

	public List<String> getKeys(String table) {
		String sql = "select " + Field.FIELD_KEY + " from " + table
				+ " group by " + Field.FIELD_KEY;
		List<ODocument> rows = db.query(new OSQLSynchQuery<ODocument>(sql));
		return Lists.transform(rows, new Function<ODocument, String>() {
			@Override
			public String apply(ODocument d) {
				return d.field(Field.FIELD_KEY);
			}
		});
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
			persistDocument(TABLE_DUMMY, time, id, Field.FIELD_LOGGER,
					"something.stuff", OType.STRING);
			persistDocument(TABLE_DUMMY, time, id, Field.FIELD_LOG_LEVEL,
					"INFO", OType.STRING);
			persistDocument(TABLE_DUMMY, time, id, Field.FIELD_MSG,
					"specialNumber=" + specialNumber, OType.STRING);
			persistDocument(TABLE_DUMMY, time, id, "specialNumber",
					specialNumber + "", OType.STRING);
			persistDocument(TABLE_DUMMY, time, id, "executionTimeSeconds",
					specialNumber * Math.random() + "", OType.STRING);
		}
		db.commit();
		log.info("persisted " + n
				+ " random values from the last hour to table " + TABLE_DUMMY);
	}

	public Iterable<String> getLogs(String table, long startTime,
			long finishTime) {
		OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(
				"select from " + table + " where logTimestamp between "
						+ startTime + " and " + finishTime
						+ " order by logTimestamp asc,logId asc");
		final List<ODocument> list = db.query(query);
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		final Iterator<String> it = Iterators.transform(list.iterator(),
				new Function<ODocument, String>() {

					String level;
					String logger;
					String msg;
					private Long time;

					@Override
					public String apply(ODocument d) {
						// String id = d.field(FIELD_LOG_ID);
						time = d.field(FIELD_LOG_TIMESTAMP);
						String key = d.field(Field.FIELD_KEY);
						String value = d.field(Field.FIELD_VALUE);

						if (FIELD_LOG_LEVEL.equals(key))
							this.level = value;
						if (FIELD_LOGGER.equals(key))
							this.logger = value;
						if (FIELD_MSG.equals(key))
							this.msg = value;

						if (this.level != null && this.logger != null
								&& this.msg != null) {
							StringBuilder s = new StringBuilder();
							s.append(df.format(new Date(time)));
							s.append(' ');
							s.append(this.level);
							s.append(' ');
							s.append(this.logger);
							s.append(" - ");
							s.append(this.msg);
							this.level = null;
							this.logger = null;
							this.msg = null;
							return s.toString();
						} else
							return null;
					}
				});
		return new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				return it;
			}
		};
	}
}
