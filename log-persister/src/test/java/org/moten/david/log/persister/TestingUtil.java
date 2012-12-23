package org.moten.david.log.persister;

import org.moten.david.log.core.MessageSplitter;
import org.moten.david.log.persister.config.Parser;

public class TestingUtil {

	static Parser createDefaultParser() {
		String pattern = "^(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d) +(\\S+) +(\\S+) +(\\S+)? ?- (.*)$";
		String patternGroups = "logTimestamp,logLevel,logLogger,threadName,logMsg";
		String timestampFormat = "yyyy-MM-dd HH:mm:ss.SSS";
		String timezone = "UTC";
		boolean multiline = false;
		return new Parser(pattern, patternGroups,
				MessageSplitter.MESSAGE_PATTERN_DEFAULT, timestampFormat,
				timezone, multiline);
	}

}
