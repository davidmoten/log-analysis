package org.moten.david.log;

import java.io.File;
import java.util.List;

import org.moten.david.log.config.Log;
import org.moten.david.log.config.Options;

import com.google.common.collect.Lists;

public class Main {
	public static void main(String[] args) {
		List<Log> list = Lists.newArrayList();
		list.add(new Log("cts", "/ausdev/container/logs/cts/cts.log"));
		Options options = new Options(null, null, list);
		Database db = new Database(new File("target/test4"));
		Watcher w = new Watcher(db, options);
		w.start();
	}
}
