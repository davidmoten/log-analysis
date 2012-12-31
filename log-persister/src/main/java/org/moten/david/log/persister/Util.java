package org.moten.david.log.persister;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Persiter utility methods.
 * 
 * @author dave
 * 
 */
public class Util {

	private static final Logger log = Logger.getLogger(Util.class.getName());

	/**
	 * <p>
	 * Returns all files that match the given regex file pattern. The pattern is
	 * a normal directory path followed by a regular expression for the filename
	 * itself. For example:
	 * </p>
	 * 
	 * <pre>
	 * /opt/appserver/logs/main.log\.*
	 * </pre>
	 * <p>
	 * will return all files starting with 'main.log.' in the
	 * /opt/appserver/logs directory.
	 * </p>
	 * <p>
	 * If directory not found a {@link RuntimeException} is returned.
	 * 
	 * @param pattern
	 * @return
	 */
	static List<File> getFilesFromPathWithRegexFilename(String s) {
		String directory = getPath(s);
		String filenameRegex = getFilename(s);
		final Pattern pattern = Pattern.compile(filenameRegex);
		log.info("directory=" + directory + ",filenameRegex=" + filenameRegex);

		File directoryFile = new File(directory);
		File[] files = directoryFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return pattern.matcher(name).matches();
			}
		});
		if (files == null)
			throw new RuntimeException("directory not found: " + directoryFile);
		return Arrays.asList(files);
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
