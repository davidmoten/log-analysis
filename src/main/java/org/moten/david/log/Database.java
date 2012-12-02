package org.moten.david.log;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.moten.david.log.query.NumericQuery;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.OStorage;

public class Database {

	private static final String FIELD_LOG_TIMESTAMP = "logTimestamp";

	public Database(String name) {
		OGlobalConfiguration.STORAGE_KEEP_OPEN.setValue(true);
		OGlobalConfiguration.MVRBTREE_NODE_PAGE_SIZE.setValue(2048);
		OGlobalConfiguration.TX_USE_LOG.setValue(false);
		OGlobalConfiguration.TX_COMMIT_SYNCH.setValue(true);
		OGlobalConfiguration.ENVIRONMENT_CONCURRENT.setValue(false);
		// OGlobalConfiguration.MVRBTREE_LAZY_UPDATES.setValue(-1);
		OGlobalConfiguration.FILE_MMAP_STRATEGY.setValue(1);
		try {
			FileUtils.deleteDirectory(new File("target/" + name));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String workingDirectory = System.getProperty("user.dir");
		String url = "local:" + workingDirectory + "/target/" + name;
		System.out.println(url);
		ODatabaseDocumentTx db = new ODatabaseDocumentTx(url).create();
		OClass user = db
				.getMetadata()
				.getSchema()
				.createClass("Entry",
						db.addCluster("entry", OStorage.CLUSTER_TYPE.PHYSICAL));
		user.createProperty(FIELD_LOG_TIMESTAMP, OType.LONG).setMandatory(true);

		db.getMetadata().getSchema().save();
		user.createIndex("LogTimestampIndex", OClass.INDEX_TYPE.NOTUNIQUE,
				FIELD_LOG_TIMESTAMP);
		db.commit();
	}

	public void persist(LogEntry entry) {
		ODocument d = new ODocument("Message");
		d.field(FIELD_LOG_TIMESTAMP, entry.getTime());
		for (Entry<String, String> e : entry.getProperties().entrySet()) {
			if (e.getValue() != null)
				d.field(e.getKey(), e.getValue());
		}
		d.save();
	}

	public Iterable<Double> execute(NumericQuery query) {

		return null;
	}
}
