package jarden.explorer;

import java.io.File;
import java.util.ArrayList;

public class ExplorerTools {
	private static final int MAX_HITS_DEFAULT = 20;
	
	/**
	 * Convenience method that calls {@link #find(File, String, int)} but
	 * with a default of 20 for maxHits.
	 * @param directory
	 * @param searchName
	 * @return
	 */
	public static ArrayList<File> find(File directory, String searchName) {
		return find(directory, searchName, MAX_HITS_DEFAULT);
	}
	/**
	 * Return all files in directory and all its sub-directories recursively
	 * where the file names match the searchName. The searchName can contain
	 * any number of '*' chars, which represent zero or more of any chars, e.g.
	 * 			*.mp3		all file names ending in '.mp3'
	 * 			*android*	all file names containing 'android'
	 * @param directory
	 * @param searchName
	 * @param maxHits - the search will stop when this number of matches
	 * 		have been found
	 * @return 
	 */
	public static ArrayList<File> find(File directory, String searchName,
			int maxHits) {
		if (directory == null || !directory.isDirectory()) return null;
	    String regex = ("\\Q" + searchName + "\\E").replace("*", "\\E.*\\Q");
	    System.out.println(regex);
	    ArrayList<File> fileList = new ArrayList<File>();
	    findRegexMatches(directory, regex, fileList, maxHits);
	    return fileList;
	}
	/**
	 * Convenience method that calls {@link #findFileNames(File, String, int)}
	 * i.e. defaults to maxHits of 20.
	 * @param directory
	 * @param searchName
	 * @return
	 */
	public static String[] findFileNames(File directory, String searchName) {
		return findFileNames(directory, searchName, MAX_HITS_DEFAULT);
	}
	/**
	 * As for {@link #find(File, String, int)} but convert the results
	 * into a String array, where each entry is the absolute path of the
	 * matching file.
	 * @param directory
	 * @param searchName
	 * @param maxHits
	 * @return
	 */
	public static String[] findFileNames(File directory, String searchName,
			int maxHits) {
		ArrayList<File> fileList = find(directory, searchName, maxHits);
		int size = fileList.size();
		String[] fileNames = new String[size];
		for (int i = 0; i < size; i++) {
			File file = fileList.get(i);
			String isDir = file.isDirectory()?"D: ":"";
			fileNames[i] = isDir + fileList.get(i).getAbsolutePath();
		}
		return fileNames;
	}
	private static void findRegexMatches(File directory, String regex,
			ArrayList<File> fileList, int maxHits) {
		String[] fileNames = directory.list();
		if (fileNames != null) {
			for (String fileName: fileNames) {
				File file = new File(directory, fileName);
				if (fileName.matches(regex)) {
					fileList.add(file);
					if (fileList.size() >= maxHits) return;
				}
				if (file.isDirectory()) {
					findRegexMatches(file, regex, fileList, maxHits);
					if (fileList.size() >= maxHits) return;
				}
			}
		}
	}


}
