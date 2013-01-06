package org.moten.david.log.orientdb;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.storage.OStorage;

public class ContainsTextTest {

	private static final String FIELD_TEXT = "text";
	private static final String TABLE = "Test1";
	private static ODatabaseDocumentTx db;

	@BeforeClass
	public static void prepare() {
		// dedicate a directory to contain the database
		String filename = "target/test-contains-text-1";
		// delete the directory so we can rerun the test
		deleteDirectory(filename);

		String url = "local:" + filename;
		db = new ODatabaseDocumentTx(url).create();
		ODatabaseRecordThreadLocal.INSTANCE.set(db);

		// create a fulltext index on Test1.text
		OSchema schema = db.getMetadata().getSchema();
		OClass table = schema.createClass(TABLE,
				db.addCluster(TABLE, OStorage.CLUSTER_TYPE.PHYSICAL));
		table.createProperty(FIELD_TEXT, OType.STRING);
		table.createIndex("TextIndex", OClass.INDEX_TYPE.FULLTEXT, FIELD_TEXT);
		db.commit();

		save("hello there how are you");
		save("underneath is the story for you");
		save("over.table spend");
	}

	@AfterClass
	public static void close() {
		ODatabaseRecordThreadLocal.INSTANCE.set(db);
		db.close();
	}

	private static void save(String text) {
		ODocument d = new ODocument(TABLE);
		d.field(FIELD_TEXT, text, OType.STRING);
		d.save();
	}

	private static void deleteDirectory(String filename) {
		try {
			FileUtils.deleteDirectory(new File(filename));
		} catch (IOException e) {
			// do nothing
		}
	}

	// TODO enable as unit test once is #1255 is fixed in orientdb
	// @Test
	public void testCountUsingContainsTextOperator() {
		List<ODocument> list = db.query(new OSQLSynchQuery<ODocument>(
				"select count(*) from Test1 where text containstext 'you'"));
		Object count = list.get(0).field("count");
		assertEquals(2, count);
	}

	@Test
	public void testContainsTextOnPeriodDelimiter() {
		List<ODocument> list = db.query(new OSQLSynchQuery<ODocument>(
				"select * from Test1 where text containstext 'table'"));
		assertEquals(1, list.size());
	}

	@Test
	public void testContainsTextFindsOneMatchInFirstRecordSavedAtStartOfText() {
		checkMatchesCountIs("hello", 1);
	}

	@Test
	public void testContainsTextFindsOneMatchInFirstRecordSavedAgainstSecondWordInText() {
		checkMatchesCountIs("there", 1);
	}

	@Test
	public void testContainsTextFindsOneMatchInSecondRecordSavedAgainstFirstWordInText() {
		checkMatchesCountIs("underneath", 1);
	}

	@Test
	public void testContainsTextFindsOneMatchInSecondRecordSavedAgainstSecondWordInText() {
		checkMatchesCountIs("story", 1);
	}

	@Test
	public void testContainsTextFindsTwoMatchesInBothRecordsSavedAgainstLastWordInText() {
		checkMatchesCountIs("you", 2);
	}

	private void checkMatchesCountIs(String word, int expectedCount) {
		List<ODocument> list = db.query(new OSQLSynchQuery<ODocument>(
				"select * from Test1 where text containstext '" + word + "'"));
		assertEquals(expectedCount, list.size());
	}
}
