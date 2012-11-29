package org.moten.david.log;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class Persister {

	public Persister(String name) {
		try {
			FileUtils.deleteDirectory(new File("target/" + name));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String workingDirectory = System.getProperty("user.dir");
		String url = "local:" + workingDirectory + "/target/" + name;
		System.out.println(url);
		new ODatabaseDocumentTx(url).create();
	}

	public void persist(LogEntry entry) {
		ODocument d = new ODocument("Message");
		d.field("logTimestamp", entry.getTime());
		for (Entry<String, String> e : entry.getProperties().entrySet()) {
			if (e.getValue() != null)
				d.field(e.getKey(), e.getValue());
		}
		d.save();
	}
}
