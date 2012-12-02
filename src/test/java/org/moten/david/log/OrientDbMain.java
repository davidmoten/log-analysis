package org.moten.david.log;

import java.util.List;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.storage.OStorage;

public class OrientDbMain {

	public static void main(String[] args) {
		OGlobalConfiguration.STORAGE_KEEP_OPEN.setValue(true);
		OGlobalConfiguration.MVRBTREE_NODE_PAGE_SIZE.setValue(2048);
		OGlobalConfiguration.TX_USE_LOG.setValue(false);
		OGlobalConfiguration.TX_COMMIT_SYNCH.setValue(true);
		OGlobalConfiguration.ENVIRONMENT_CONCURRENT.setValue(false);
		// OGlobalConfiguration.MVRBTREE_LAZY_UPDATES.setValue(-1);
		OGlobalConfiguration.FILE_MMAP_STRATEGY.setValue(1);

		String sessionId = "session";// "session-" + (int) (Math.random() *
										// 100.0d);
		System.out.println("orient-db: load testing; sessionId: " + sessionId);

		int singleDocSelectCount = 10000;
		int selectRangeCount = 100;

		int initialDocCount = 25000;
		int docCountStride = 25000;
		int maxDocCount = 500000;
		for (int docCount = initialDocCount; docCount <= maxDocCount; docCount += docCountStride) {
			ODatabaseDocumentTx db = new ODatabaseDocumentTx(
					"local:/tmp/orientdb/data/" + sessionId);
			if (db.exists())
				db.delete();
			db.create();

			OClass user = db
					.getMetadata()
					.getSchema()
					.createClass(
							"User",
							db.addCluster("user",
									OStorage.CLUSTER_TYPE.PHYSICAL));
			user.createProperty("id", OType.INTEGER).setMandatory(true);

			db.getMetadata().getSchema().save();
			user.createIndex("UserIdIndex", OClass.INDEX_TYPE.UNIQUE, "id");
			db.commit();

			db.declareIntent(new OIntentMassiveInsert());

			// insert docs...
			double insertThroughput = 0.0d;
			{
				long start = System.currentTimeMillis();
				for (int i = 0; i < docCount; i++) {
					ODocument doc = new ODocument(db, "User");

					String json = getDoc(i);
					doc.fromJSON(json);
					doc.setClassName("User");

					doc.save();
				}
				long end = System.currentTimeMillis();
				double duration = (end - start) / 1000.0d;
				insertThroughput = docCount / duration;
			}

			db.declareIntent(null);

			// query single doc...
			double selectSingleThroughput = 0.0d;
			{
				long start = System.currentTimeMillis();
				for (int i = 0; i < singleDocSelectCount; i++) {
					int startId = (int) (Math.random() * singleDocSelectCount);
					OSQLSynchQuery<ODocument> sqlQuery = new OSQLSynchQuery<ODocument>(
							"select * from User where id = " + startId);
					List<ODocument> result = db.query(sqlQuery);
				}
				long end = System.currentTimeMillis();
				double duration = (end - start) / 1000.0d;
				selectSingleThroughput = singleDocSelectCount / duration;
			}

			// query doc range...
			double selectRangeThroughput = 0.0d;
			{
				long start = System.currentTimeMillis();
				for (int i = 0; i < selectRangeCount; i++) {
					int startId = (int) (Math.random() * selectRangeCount);
					OSQLSynchQuery<ODocument> sqlQuery = new OSQLSynchQuery<ODocument>(
							"select * from User where id > " + startId
									+ " limit 20");
					List<ODocument> result = db.query(sqlQuery);
				}
				long end = System.currentTimeMillis();
				double duration = (end - start) / 1000.0d;
				selectRangeThroughput = selectRangeCount / duration;
			}

			System.out
					.println("docCount: " + docCount + "; insertThroughput: "
							+ (int) insertThroughput
							+ "; selectSingleThroughput: "
							+ (int) selectSingleThroughput
							+ "; selectRangeThroughput: "
							+ (int) selectRangeThroughput);

			db.close();
		}

		System.out.println("orient-db: completed");
	}

	static String getDoc(int id) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{ \"id\": ");
		stringBuilder.append(id);
		// stringBuilder.append(", \"type\": \"testData\"");

		stringBuilder.append(", \"data\": { ");

		stringBuilder.append("\"name1\":");
		stringBuilder.append("\"value");
		int randomValue1 = (int) (Math.random() * 10.0);
		stringBuilder.append(randomValue1);

		stringBuilder.append("\", \"name2\":");
		stringBuilder.append("\"value");
		int randomValue2 = (int) (Math.random() * 10.0);
		stringBuilder.append(randomValue2);
		stringBuilder.append("\"");

		stringBuilder.append(", \"subdata\": { ");

		stringBuilder.append("\"name3\":");
		stringBuilder.append("\"value");
		int randomValue3 = (int) (Math.random() * 10.0);
		stringBuilder.append(randomValue3);

		stringBuilder.append("\", \"name4\":");
		stringBuilder.append("\"value");
		int randomValue4 = (int) (Math.random() * 10.0);
		stringBuilder.append(randomValue4);
		stringBuilder.append("\"");

		stringBuilder.append(" } ");

		stringBuilder.append(" }");
		stringBuilder.append(" }");

		String json = stringBuilder.toString();
		return json;
	}
}