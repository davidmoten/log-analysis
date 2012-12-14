package org.moten.david.log.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.moten.david.log.query.BucketQuery;
import org.moten.david.log.query.Buckets;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.storage.OStorage;

public class Database {

	private static final Logger log = Logger
			.getLogger(Database.class.getName());

	public static final String FIELD_LOG_TIMESTAMP = "logTimestamp";
	private static final String TABLE_ENTRY = "Entry";
	private static final String FIELD_VALUE = "value";

	private static final String TABLE_DUMMY = "Dummy";

	private ODatabaseDocumentTx db;
	private final MessageSplitter splitter = new MessageSplitter();

	private String url;

	private String username;

	private String password;

	public Database(File location) {
		this(connectToDatabase(location));
	}

	public Database(String url, String username, String password) {
		this(connectToDatabase(url, username, password));
		this.url = url;
		this.username = username;
		this.password = password;
	}

	private static ODatabaseDocumentTx connectToDatabase(String url,
			String username, String password) {
		ODatabaseDocumentTx db = new ODatabaseDocumentTx(url).open(username,
				password);
		log.info("obtained db for " + url);
		configureDatabase(db);
		return db;
	}

	public Database(ODatabaseDocumentTx db) {
		this.db = db;
	}

	private static ODatabaseDocumentTx connectToDatabase(File location) {
		OGlobalConfiguration.STORAGE_KEEP_OPEN.setValue(true);
		OGlobalConfiguration.MVRBTREE_NODE_PAGE_SIZE.setValue(2048);
		OGlobalConfiguration.TX_USE_LOG.setValue(false);
		OGlobalConfiguration.TX_COMMIT_SYNCH.setValue(true);
		OGlobalConfiguration.ENVIRONMENT_CONCURRENT.setValue(false);
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
		configureDatabase(db);
		return db;
	}

	private static void configureDatabase(ODatabaseDocumentTx db) {
		try {
			OClass user = db
					.getMetadata()
					.getSchema()
					.createClass(
							TABLE_ENTRY,
							db.addCluster(TABLE_ENTRY,
									OStorage.CLUSTER_TYPE.PHYSICAL));
			user.createProperty(FIELD_LOG_TIMESTAMP, OType.LONG).setMandatory(
					true);

			db.getMetadata().getSchema().save();
			user.createIndex("LogTimestampIndex", OClass.INDEX_TYPE.NOTUNIQUE,
					FIELD_LOG_TIMESTAMP);
			db.commit();
		} catch (RuntimeException e) {
			log.log(Level.WARNING, e.getMessage());
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
		ODocument d = new ODocument(TABLE_ENTRY);

		persist(entry, d);
	}

	private void persist(LogEntry entry, ODocument d) {
		try {
			// persist the full message, timestamp, level logger and threadName
			d.field(FIELD_LOG_TIMESTAMP, entry.getTime());
			for (Entry<String, String> e : entry.getProperties().entrySet()) {
				if (e.getValue() != null)
					d.field(e.getKey(), e.getValue());
			}

			// persist the split fields from the full message
			Map<String, String> map = splitter.split(entry.getMessage());
			// System.out.println(entry);
			if (map.size() > 0)
				log.info(map.toString());
			for (Entry<String, String> e : map.entrySet()) {
				if (e.getValue() != null) {
					// field names in orientdb cannot have spaces so replace
					// them
					// with underscores
					ValueAndType v = parse(e.getValue());
					d.field(e.getKey().replace(" ", "_"), v.value, v.type);
				}
			}

			// persist the document
			d.save();
		} catch (RuntimeException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			try {
				log.info("sleeping for 10 seconds before attempting reconnect");
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// do nothing
			}
			connect();
		}
	}

	private void connect() {
		try {
			log.info("closing existing db connection");
			db.close();
		} catch (RuntimeException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}
		db = connectToDatabase(url, username, password);
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
		System.out.println(query);
		OSQLSynchQuery<ODocument> sqlQuery = new OSQLSynchQuery<ODocument>(
				query.getSql());
		List<ODocument> result = db.query(sqlQuery);
		Buckets buckets = new Buckets(query);
		for (ODocument doc : result) {
			System.out.println(doc);
			Long timestamp = doc.field(FIELD_LOG_TIMESTAMP);
			Number value = doc.field(FIELD_VALUE);
			buckets.add(timestamp, value.doubleValue());
		}
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
		for (int i = 0; i < 1000; i++) {
			long time = t - TimeUnit.HOURS.toMillis(1)
					+ r.nextInt((int) TimeUnit.HOURS.toMillis(2));
			ODocument d = new ODocument(TABLE_DUMMY);
			int specialNumber = i % (r.nextInt(100) + 1);
			d.field(LogParser.FIELD_LOG_TIMESTAMP, time, OType.LONG);
			d.field(LogParser.FIELD_MSG, "specialNumber=" + specialNumber);
			d.field("specialNumber", specialNumber);
			d.save();
		}
		log.info("persisted 1000 random values from the last hour to table Dummy");
	}
}
