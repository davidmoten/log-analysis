package org.moten.david.log.persister;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.tools.ant.DirectoryScanner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
		String filenameRegex = getFilename(s);
		String directoryPath = getDirectory(s);
		System.out.println(directoryPath + ":" + filenameRegex);
		final Pattern pattern = Pattern.compile(filenameRegex);
		List<File> directories = getDirectories(directoryPath);
		System.out.println("dirs=" + directories);
		List<File> result = Lists.newArrayList();
		for (File d : directories) {
			File[] fileList = d.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return !file.isDirectory()
							&& pattern.matcher(file.getName()).matches();
				}
			});
			if (fileList != null)
				for (File f : fileList)
					result.add(f);
		}
		return result;
	}

	private static List<File> getDirectories(String directoryPath) {
		Set<File> directories = Sets.newHashSet();
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(new String[] { directoryPath });
		if (directoryPath.startsWith("/"))
			scanner.setBasedir("/");
		else
			scanner.setBasedir(System.getProperty("user.dir"));
		scanner.setCaseSensitive(false);
		scanner.scan();
		String[] paths = scanner.getIncludedFiles();
		for (String p : paths) {
			File file = new File(p);
			directories.add(file.getParentFile());
		}
		return Lists.newArrayList(directories);
	}

	/**
	 * Returns the directory of the given file path with a terminating '/'. If
	 * no directory specified returns blank string.
	 * 
	 * @param path
	 * @return
	 */
	static String getDirectory(String path) {
		// don't use System.getProperty("file.separator") because \ may be used
		// as part of regex.
		int i = path.lastIndexOf("/");
		if (i == -1)
			return "";
		else
			return path.substring(0, i + 1);
	}

	/**
	 * Returns the filename portion of a file path. If no directory specified
	 * returns the given path as the filename.
	 * 
	 * @param path
	 * @return
	 */
	static String getFilename(String path) {
		// don't use System.getProperty("file.separator") because \ may be used
		// as part of regex.
		int i = path.lastIndexOf("/");
		if (i == -1)
			return path;
		else
			return path.substring(i + 1, path.length());
	}

}
