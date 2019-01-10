package thread.jdk5.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import jarden.http.MyHttpClient;

/**
 * Demo program to show networking, threading and logging.
 * Can also be used to show code-signing security:
 * 		see demo.security.SecurityNotes.txt
 * Create a local cache of files downloaded from my web site.
 * Main methods: getFileList(), getFile(String fileName) and
 * getFiles(String[] fileNames). All these methods run in background
 * thread, and pass results back to ResultListener object passed to
 * constructor of ProblemCache.
 * Files are cached locally on disc. A Swing program will display
 * the contents of the selected file.
 * All actions and errors are recorded in the logging file.
 * To use the logging configuration file in docs/myLogging.properties, add
 *     -Djava.util.logging.config.file=docs/myLogging.properties
 * to VM arguments of ProblemSwing. 
 * 
 * 
 * @author john.denny@gmail.com
 */
public class ProblemCache {
	public static interface ResultListener {
		void onFileNameList(List<String> fileNames);
		void onFileRetrieved(String fileContent);
	}
	private final static String PROBLEM_URL =
			"https://sites.google.com/site/amazequiz/home/problems/";
	private final static String FILE_LIST_URL = PROBLEM_URL + "fileList.txt";
	private final static String CLASS_NAME = ProblemCache.class.getName();

	private ResultListener resultListener;
	private Logger logger;
	private Executor executor;
	
	public ProblemCache(ResultListener resultListener) {
		this.resultListener = resultListener;
		this.logger = Logger.getLogger(CLASS_NAME);
		this.executor = Executors.newCachedThreadPool();
	}
	public void getFileList() {
		this.logger.entering(CLASS_NAME, "getFileList");
		this.executor.execute(new Runnable() {
			@Override
			public void run() {
				logger.entering(CLASS_NAME, "getFileList.run(); threadName: " +
						Thread.currentThread().getName());
				ArrayList<String> fileNames;
				try {
					fileNames = MyHttpClient.getPageLines(FILE_LIST_URL);
					logger.info("fileList obtained from server");
				} catch (IOException e) {
					logger.warning("ioexception trying to access server: " + e);
					logger.info("getting fileList from local disc");
					String[] tempList = {
							"one", "two", "three", "four",
							"can I have a little more"		};
					fileNames = new ArrayList<String>(Arrays.asList(tempList));
				/* TODO: if site not available, retrieve from local disc
				 */
				}
				resultListener.onFileNameList(fileNames);
			}
		});
	}
	public void getFile(String fileName) {
		this.logger.entering(CLASS_NAME, "getFile");
		this.executor.execute(new GetFileRunnable(fileName));
	}
	public void getFiles(String[] fileNames) {
		for (String fileName: fileNames) {
			getFile(fileName);
		}
	}
	private class GetFileRunnable implements Runnable {
		private String fileName;

		public GetFileRunnable(String fileName) {
			logger.entering("GetFileRunnable", "GetFileRunnable(" +
					fileName + ")");
			this.fileName = fileName;
		}
		@Override
		public void run() {
			logger.entering("GetFileRunnable", "run(); threadName: " +
					Thread.currentThread().getName());
			String fileContent;
			try {
				fileContent = MyHttpClient.getPage(PROBLEM_URL + fileName);
			} catch (IOException e) {
				logger.warning("ioexception trying to access server: " + e);
				logger.info("getting file from local disc");
				// TODO: get named file from local disc
				fileContent = "Q: table\nA: mesa\nQ: chair\nA: asiento";
			}
			resultListener.onFileRetrieved(fileContent);
		}
	}
}
