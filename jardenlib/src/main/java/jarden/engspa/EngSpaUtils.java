package jarden.engspa;

import android.app.Activity;
import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jarden.provider.engspa.EngSpaContract;

public class EngSpaUtils {
	private final static String TAG = "EngSpaUtils";

	/**
	 * Process lines from inputStream, in the form:
	 * 		house,casa,noun,feminine,building,1
	 * and return ContentValues suitable for loading into EngSpa database.
	 * Note: don't run this method on the UI thread!
	 * @throws IOException
	 */

	public static ContentValues[] getContentValuesArray(InputStream is) throws IOException {
		List<String> engSpaLines = getLinesFromStream(is);
		return getContentValuesArray(engSpaLines);
	}
	public static List<String> getLinesFromStream(InputStream is) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			// iso-8859 needed for Android, and maybe for Java;
			// see javadocs in QuizCache.loadQuizFromServer()
			InputStreamReader isReader = new InputStreamReader(is, "iso-8859-1");
			reader = new BufferedReader(isReader);
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		} finally {
			is.close();
		}
	}
	public static ContentValues[] getContentValuesArray(List<String> engSpaLines) {
		ArrayList<ContentValues> contentValuesList = new ArrayList<ContentValues>();
		ContentValues contentValues;
		for (String engSpaLine: engSpaLines) {
			contentValues = getContentValues(engSpaLine);
			if (contentValues != null) {
				contentValuesList.add(contentValues);
			}
		}
		return contentValuesList.toArray(new ContentValues[contentValuesList.size()]);
	}
	public static ContentValues getContentValues(String engSpaLine) {
		String[] tokens = engSpaLine.split(",");
		if (tokens.length == 6) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(EngSpaContract.ENGLISH, tokens[0]);
			contentValues.put(EngSpaContract.SPANISH, tokens[1]);
			contentValues.put(EngSpaContract.WORD_TYPE, tokens[2]);
			contentValues.put(EngSpaContract.QUALIFIER, tokens[3]);
			contentValues.put(EngSpaContract.ATTRIBUTE, tokens[4]);
			contentValues.put(EngSpaContract.LEVEL, tokens[5]);
			return contentValues;
		} else {
            Log.e(TAG, "line from bulk update file doesn't contain 6 tokens: " +
                    engSpaLine);
			return null;
		}
	}
	public interface RunnableWithException extends Runnable {
		void setResult(boolean success, Exception exception);
	}
	public static void runBackgroundTask(final Activity activity, final Runnable backgroundRun,
										 final RunnableWithException foregroundRun) {
		new Thread(new Runnable() {
			private boolean success;
			private Exception exception;
			@Override public void run() {
				try {
					backgroundRun.run();
					this.success = true;
				} catch(Exception e) {
					this.success = false;
					this.exception = e;
				}
				foregroundRun.setResult(success, exception);
				activity.runOnUiThread(foregroundRun);
			}
		}).start();
	}

}
