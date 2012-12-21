package org.moten.david.log.persister;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.moten.david.log.configuration.Log;

import com.google.common.collect.Lists;

public class Util {

	private static final Logger log = Logger.getLogger(Util.class.getName());

	/**
	 * Note that regex special characters can only be used on the filename not
	 * the containing directory.
	 * 
	 * @param name
	 * @param regexPaths
	 * @return
	 */
	static List<Log> getLogs(String[] regexPaths) {
		List<Log> list = Lists.newArrayList();
		for (String item : regexPaths) {
			Log lg = getLog(item);
			if (lg != null)
				list.add(lg);
		}
		return list;
	}

	static List<File> getFilesFromPathWithRegexFilename(String item) {
		String directory = getPath(item);
		String filenameRegex = getFilename(item);
		final Pattern pattern = Pattern.compile(filenameRegex);
		log.info("directory=" + directory + ",filenameRegex=" + filenameRegex);

		File directoryFile = new File(directory);
		File[] files = directoryFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return pattern.matcher(name).matches();
			}
		});
		return Arrays.asList(files);
	}

	private static Log getLog(String item) {
		Log lg = null;
		List<File> files = getFilesFromPathWithRegexFilename(item);
		if (files != null)
			for (File file : files) {
				lg = new Log(file.getPath(), true);
				log.info("added " + file);
			}
		return lg;
	}

	static String getPath(String item) {
		// don't use System.getProperty("file.separator") because \ may be used
		// as part of regex.
		int i = item.lastIndexOf("/");
		if (i == -1)
			return "";
		else
			return item.substring(0, i + 1);
	}

	static String getFilename(String item) {
		// don't use System.getProperty("file.separator") because \ may be used
		// as part of regex.
		int i = item.lastIndexOf("/");
		if (i == -1)
			return item;
		else
			return item.substring(i + 1, item.length());
	}

}
