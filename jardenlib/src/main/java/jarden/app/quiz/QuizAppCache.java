package jarden.app.quiz;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

import jarden.quiz.QuizCache;

public class QuizAppCache extends QuizCache {
	private Context context;
	private String[] localFileNames = null;
	private final static boolean DEBUG = true;
	private final static String TAG = "QuizAppCache";
	
	public QuizAppCache(Context context) {
		setContext(context);
	}
	public void setContext(Context context) {
		this.context = context;
	}
	@Override
	public String[] getLocalFileNames() {
		if (localFileNames == null) {
			localFileNames = context.fileList();
		}
		return localFileNames;
	}
	@Override
	public FileInputStream getFileInputStream(String fileName)
			throws IOException {
		return context.openFileInput(fileName);
	}
	@Override
	public FileOutputStream getFileOutputStream(String fileName)
			throws IOException {
		return context.openFileOutput(fileName, Context.MODE_PRIVATE);
	}

	@Override
	public void logCacheMessage(String message) {
		if (DEBUG) {
			Log.d(TAG, message);
		}
	}
}
