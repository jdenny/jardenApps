package jarden.quiz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AmazeQuizCache extends QuizCache {
	private final static String localDirName = "/Users/john/temp/amazeQuizCache";
	private String[] localFileNames = null;
	private File localDir;
	private QuizCacheListener quizCacheListener;
	
	public AmazeQuizCache(QuizCacheListener quizCacheListener) {
		this.quizCacheListener = quizCacheListener;
		localDir = new File(localDirName);
		if (!localDir.exists()) {
			localDir.mkdir();
		}
	}

	@Override
	public String[] getLocalFileNames() {
		if (localFileNames == null) {
			localFileNames = localDir.list();
		}
		return localFileNames;
	}

	@Override
	public FileInputStream getFileInputStream(String fileName) throws IOException {
		File file = new File(localDir, fileName);
		return new FileInputStream(file);
	}

	@Override
	public FileOutputStream getFileOutputStream(String fileName) throws IOException {
		File file = new File(localDir, fileName);
		return new FileOutputStream(file);
	}

	@Override
	public void logCacheMessage(String message) {
		quizCacheListener.onLogMessage(message);
		System.out.println(message);
	}
}
