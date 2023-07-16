package jarden.engspa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.jardenconsulting.jardenlib.BuildConfig;
import com.jardenconsulting.jardenlib.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jarden.provider.engspa.EngSpaContract.Topic;
import jarden.provider.engspa.EngSpaContract.QAStyle;
import jarden.provider.engspa.EngSpaContract.Qualifier;
import jarden.provider.engspa.EngSpaContract.WordType;

import static jarden.provider.engspa.EngSpaContract.ATTRIBUTE;
import static jarden.provider.engspa.EngSpaContract.CONSEC_RIGHT_CT;
import static jarden.provider.engspa.EngSpaContract.ENGLISH;
import static jarden.provider.engspa.EngSpaContract.FAILED_WORD_VIEW;
import static jarden.provider.engspa.EngSpaContract.LEVEL;
import static jarden.provider.engspa.EngSpaContract.NAME;
import static jarden.provider.engspa.EngSpaContract.PROJECTION_ALL_FAILED_WORD_FIELDS;
import static jarden.provider.engspa.EngSpaContract.PROJECTION_ALL_FIELDS;
import static jarden.provider.engspa.EngSpaContract.QA_STYLE;
import static jarden.provider.engspa.EngSpaContract.QUALIFIER;
import static jarden.provider.engspa.EngSpaContract.QUESTION_SEQUENCE;
import static jarden.provider.engspa.EngSpaContract.SPANISH;
import static jarden.provider.engspa.EngSpaContract.TABLE;
import static jarden.provider.engspa.EngSpaContract.USER_ID;
import static jarden.provider.engspa.EngSpaContract.USER_TABLE;
import static jarden.provider.engspa.EngSpaContract.USER_WORD_TABLE;
import static jarden.provider.engspa.EngSpaContract.WORD_ID;
import static jarden.provider.engspa.EngSpaContract.WORD_TYPE;

public class EngSpaSQLite2 extends SQLiteOpenHelper implements EngSpaDAO {
	private static EngSpaSQLite2 instance;
	private static final String DB_NAME = "engspa.db";
	// Note: if we update DB_VERSION, also update res/raw/engspaversion.txt
	private static final int DB_VERSION =
			45; // updated 18 November 2018; now update engspaversion.txt!

	private static final String TAG = "EngSpaSQLite";
	private static final String CREATE_TABLE =
		"CREATE TABLE " + TABLE + " (" +
		BaseColumns._ID +          " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
		ENGLISH +   " TEXT NOT NULL, " +
		SPANISH +   " TEXT NOT NULL, " +
		WORD_TYPE + " TEXT NOT NULL, " +
		QUALIFIER + " TEXT NOT NULL, " +
		ATTRIBUTE + " TEXT NOT NULL, " +
		LEVEL +     " INTEGER NOT NULL);";
	private static final String CREATE_USER_TABLE =
		"CREATE TABLE " + USER_TABLE + " (" +
		BaseColumns._ID +          " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
		NAME +      " TEXT NOT NULL, " +
		LEVEL +     " INTEGER, " +
		QA_STYLE +  " TEXT NOT NULL);";
	private static final String CREATE_USER_WORD_TABLE =
		"CREATE TABLE " + USER_WORD_TABLE + " (" +
		USER_ID +   " INTEGER NOT NULL, " +
		WORD_ID +   " INTEGER NOT NULL, " +
		CONSEC_RIGHT_CT +  " INTEGER NOT NULL, " +
		QUESTION_SEQUENCE + " INTEGER NOT NULL, " +
		QA_STYLE + " TEXT NOT NULL, PRIMARY KEY (" +
		USER_ID + "," + WORD_ID + ") );";
	private static final String CREATE_ATTRIBUTE_INDEX =
		"CREATE INDEX attributeIndex ON " + TABLE + " (" + ATTRIBUTE + ");";
	private static final String CREATE_FAILED_WORD_VIEW =
		"CREATE VIEW " + FAILED_WORD_VIEW + " AS SELECT " +
				"es." + BaseColumns._ID +
				",es." + ENGLISH +
				",es." + SPANISH +
				",es." + WORD_TYPE +
				",es." + QUALIFIER +
				",es." + ATTRIBUTE +
				",es." + LEVEL +
				",uw." + USER_ID +
				",uw." + CONSEC_RIGHT_CT +
				",uw." + QUESTION_SEQUENCE +
				",uw." + QA_STYLE +
				" from " + TABLE + " AS es, " +
				USER_WORD_TABLE + " AS uw where es." + BaseColumns._ID +
				"=uw." + WORD_ID;
	private static final String DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE;
	private static final String DROP_USER_TABLE =
			"DROP TABLE IF EXISTS " + USER_TABLE;
	private static final String DROP_USER_WORD_TABLE =
			"DROP TABLE IF EXISTS " + USER_WORD_TABLE;
	private static final String DROP_FAILED_WORD_VIEW =
			"DROP VIEW IF EXISTS " + FAILED_WORD_VIEW;
	private static final String RESET_SEQUENCE_NUMBER =
			"update SQLITE_SEQUENCE set SEQ = 0 where NAME = '" +
			TABLE + "'";
		// or: delete from SQLITE_SEQUENCE where NAME = '" + TABLE + "'"

	private static final String SELECTION = BaseColumns._ID + "=?";
	private static final String USER_WORD_SELECTION = USER_ID + "=1 and " + WORD_ID + "=?";

	private Context context;
	private int dictionarySize = 0;
	private Random random = new Random();

	public static synchronized EngSpaSQLite2 getInstance(Context context) {
		if (instance == null) {
			instance = new EngSpaSQLite2(context);
		}
		return instance;
	}
	private EngSpaSQLite2(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override // SQLiteOpenHelper
	public void onCreate(SQLiteDatabase engSpaDB) {
		Log.i(TAG, "onCreate()");
		engSpaDB.execSQL(CREATE_TABLE);
		engSpaDB.execSQL(CREATE_ATTRIBUTE_INDEX);
		engSpaDB.execSQL(CREATE_USER_TABLE);
		engSpaDB.execSQL(CREATE_USER_WORD_TABLE);
		engSpaDB.execSQL(CREATE_FAILED_WORD_VIEW);
	}
	
	// TODO: preserve data from user table across upgrades
	@Override // SQLiteOpenHelper
	public void onUpgrade(SQLiteDatabase engSpaDB, int oldVersion, int newVersion) {
		Log.i(TAG,
				"onUpgrade(oldVersion=" + oldVersion +
				", newVersion=" + newVersion + ")");
		engSpaDB.execSQL(DROP_FAILED_WORD_VIEW);
		engSpaDB.execSQL(DROP_TABLE); // also removes any indexes
		engSpaDB.execSQL(DROP_USER_TABLE);
		engSpaDB.execSQL(DROP_USER_WORD_TABLE);
		onCreate(engSpaDB);
	}
	
	@Override // EngSpaDAO
	public int newDictionary() {
		SQLiteDatabase engSpaDB = getWritableDatabase();
		int delRowCt = delete(engSpaDB, null, null);
		Log.i(TAG, "newDictionary(); rows deleted from database: " + delRowCt);
		return populateDatabase(engSpaDB);
	}
	
	@Override // EngSpaDAO
	public int updateDictionary(List<String> updateLines) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "updateDictionary(); updateLines.size=" +
					updateLines.size());
		int rowCount = 0;
		for (String line: updateLines) {
			if (BuildConfig.DEBUG) Log.d(TAG, "  " + line);
			try {
				if (line.startsWith("u,")) {
					int index = line.indexOf(',', 2); // get position of 2nd comma
					String idStr = line.substring(2, index);
					int id = Integer.parseInt(idStr);
					ContentValues contentValues = EngSpaUtils.getContentValues(line.substring(index + 1));
					contentValues.put(BaseColumns._ID, id);
					update(contentValues, SELECTION, new String[] { idStr });
					rowCount++;
					if (BuildConfig.DEBUG) Log.d(TAG, "id of updated row: " + id);
				} else if (line.startsWith("c,")) {
					ContentValues contentValues = EngSpaUtils.getContentValues(line.substring(2));
					long id = insert(getWritableDatabase(), contentValues);
					rowCount++;
					if (BuildConfig.DEBUG) Log.d(TAG, "id of new row: " + id);
				}
			} catch (Exception e) {
				Log.e(TAG, "updateDictionary(); exception: " + e);
			}
		}
		return rowCount;
	}

	private int populateDatabase(SQLiteDatabase engSpaDB) {
		try {
			InputStream is = context.getResources().openRawResource(R.raw.engspa);
			ContentValues[] contentValuesArray = EngSpaUtils.getContentValuesArray(is);
			this.dictionarySize = this.bulkInsert(engSpaDB, contentValuesArray);
			return this.dictionarySize; 
		} catch (IOException e) {
			Log.e(TAG, "exception in populateDatabase(): " + e);
			return 0;
		}
	}
	private boolean validateValues (ContentValues values) {
		try {
			WordType.valueOf((String) values.get(WORD_TYPE));
			Qualifier qualifier = Qualifier.valueOf((String) values.get(QUALIFIER));
			Topic.valueOf((String) values.get(ATTRIBUTE));
            if (qualifier == Qualifier.conjugate) {
                String english = (String) values.get(ENGLISH);
                String spanish = (String) values.get(SPANISH);
                checkEmbeddedToken(english);
                checkEmbeddedToken(spanish);
            }
			return true;
		} catch(Exception ex) {
			Log.e(TAG, "exception in validateValues(" + values + "): " + ex);
			return false;
		}
	}
    private void checkEmbeddedToken(String str) throws IllegalArgumentException {
        int index = str.indexOf('<');
        if (index < 0) throw new IllegalArgumentException(str + " doesn't contain '<'");
        index = str.indexOf('>', index);
        if (index < 0) throw new IllegalArgumentException(str + " doesn't contain '>'");
    }
	private long insert(SQLiteDatabase engSpaDB, ContentValues values) {
		if (validateValues(values)) {
			return engSpaDB.insert(TABLE, null, values);
		} else return -1;
	}
	private int bulkInsert(SQLiteDatabase engSpaDB, ContentValues[] valuesArray) {
		int rows = 0;
		if (valuesArray == null || valuesArray.length == 0) {
			// restoreDB from local file
			rows = delete(engSpaDB, null, null);
			Log.i(TAG, "bulkInsert(); rows deleted from database: " + rows);
			rows = populateDatabase(engSpaDB);
		} else {
			for (ContentValues contentValues: valuesArray) {
				if (insert(engSpaDB, contentValues) > 0) ++rows;
			}
		}
		Log.i(TAG, "bulkInsert(); rows added to database: " + rows);
		return rows;
	}
	private Cursor getCursor(String sql, String[] selectionArgs) {
		return getReadableDatabase().rawQuery(sql, selectionArgs);
	}
	private int delete(SQLiteDatabase engSpaDB, String selection, String[] selectionArgs) {
		int rows = engSpaDB.delete(TABLE, selection, selectionArgs);
		if (selection == null) {
			// if deleting all the rows, reset the sequence number
			engSpaDB.execSQL(RESET_SEQUENCE_NUMBER);
		}
		return rows;
	}
    // methods for Content Provider: ********************************************
    public Cursor getCursor(String[] columns, String selection,
                            String[] selectionArgs, String groupBy, String having,
                            String orderBy) {
        Cursor cursor = getReadableDatabase().query(
                TABLE, columns, selection, selectionArgs,
                groupBy, having, orderBy);
        return cursor;
    }
    public int update(ContentValues values, String selection,
                       String[] selectionArgs) {
        if (validateValues(values)) {
            return getWritableDatabase().update(TABLE, values,
                    selection, selectionArgs);
        } else return 0;
    }
    public int delete(String selection, String[] selectionArgs) {
        return delete(getWritableDatabase(), selection, selectionArgs);
    }
    public long insert(ContentValues values) {
        return insert(getWritableDatabase(), values);
    }
    public int bulkInsert(ContentValues[] values) {
        return bulkInsert(getWritableDatabase(), values);
    }

	// methods for UserWord table: **********************************************
	@Override // EngSpaDAO
	public List<EngSpa> getFailedWordList() {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"getFailedWordList()");
		Cursor cursor = null;
		try {
			cursor = getReadableDatabase().query(
					FAILED_WORD_VIEW,
					PROJECTION_ALL_FAILED_WORD_FIELDS,
					null, // selection
					null, // getUserSelectionArgs(userId), // selectionArgs
					null, // groupBy
					null, // having
					null); // orderBy
			List<EngSpa> wordList = new LinkedList<EngSpa>();
			while (cursor.moveToNext()) {
				EngSpa engSpa = engSpaFromCursor(cursor);
				engSpa.setConsecutiveRightCt(cursor.getInt(7));
				engSpa.setQuestionSequence(cursor.getInt(8));
				String qaStyleStr = cursor.getString(9); 
				engSpa.setQaStyle(QAStyle.valueOf(qaStyleStr));
				wordList.add(engSpa);
			}
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "getFailedWordList got " +
						wordList.size() + " words");
			}
			return wordList;
		} finally {
			if (cursor != null) cursor.close();
		}
	}

	@Override // EngSpaDAO
	public long insertUserWord(UserWord userWord) {
		return getWritableDatabase().insert(
				USER_WORD_TABLE, null, getContentValues(userWord));
	}
	private static ContentValues getContentValues(UserWord userWord) {
		ContentValues userWordValues = new ContentValues();
		userWordValues.put(USER_ID, 1);
		userWordValues.put(WORD_ID, userWord.getWordId());
		userWordValues.put(CONSEC_RIGHT_CT, userWord.getConsecutiveRightCt());
		userWordValues.put(QUESTION_SEQUENCE, userWord.getQuestionSequence());
		userWordValues.put(QA_STYLE, userWord.getQaStyle().toString());
		return userWordValues;
	}

	@Override // EngSpaDAO
	public int updateUserWord(UserWord userWord) {
		return getWritableDatabase().update(
				USER_WORD_TABLE,
				getContentValues(userWord),
				USER_WORD_SELECTION,
				getSelectionArgs(userWord));
	}
	@Override // EngSpaDAO
	public long replaceUserWord(UserWord userWord) {
		return getWritableDatabase().replace(
                USER_WORD_TABLE, null, getContentValues(userWord));
	}

	private static String[] getSelectionArgs(UserWord userWord) {
		return new String[] {
			Integer.toString(userWord.getWordId())
		};
	}

	@Override // EngSpaDAO
	public int deleteUserWord(UserWord userWord) {
		return getWritableDatabase().delete(
				USER_WORD_TABLE,
				USER_WORD_SELECTION,
				getSelectionArgs(userWord) );
	}
	
	@Override // EngSpaDAO
	public int deleteAllUserWords() {
		String selection;
		String[] selectionArguments;
        selection = null;
        selectionArguments = null;
		return getWritableDatabase().delete(
				USER_WORD_TABLE,
				selection,
				selectionArguments );
	}

	@Override // EngSpaDAO
	public List<EngSpa> getCurrentWordList(int userLevel) {
		if (userLevel < 1) userLevel = 1;
		int firstId = (userLevel - 1) * EngSpaQuiz.WORDS_PER_LEVEL + 1;
		int dbSize = getDictionarySize();
		if (firstId > (dbSize + 1 - EngSpaQuiz.WORDS_PER_LEVEL)) {
            if (BuildConfig.DEBUG) Log.w(TAG,
                    "getCurrentWordList(this shouldn't happen! firstId=" + firstId +
                            ", dbSize=" + dbSize + ")");
			firstId = random.nextInt(dbSize - EngSpaQuiz.WORDS_PER_LEVEL);
		}
		int lastId = firstId + EngSpaQuiz.WORDS_PER_LEVEL;
		String sql = "select * from " + TABLE +
				" where _id >= " + firstId + " and _id < " + lastId;
		if (BuildConfig.DEBUG) Log.d(TAG,
			"getCurrentWordList(" + userLevel +
			"); about to get words from " + firstId + " to " + (lastId-1));
		Cursor cursor = null;
		try {
			cursor = getCursor(sql, null);
			List<EngSpa> wordList = new LinkedList<EngSpa>();
			while (cursor.moveToNext()) {
				wordList.add(engSpaFromCursor(cursor));
			}
			if (BuildConfig.DEBUG) Log.d(TAG,
					"getCurrentWordList(); words obtains: " +
					wordList.size());
			return wordList;
		} finally {
			if (cursor != null) cursor.close();
		}
	}
	private EngSpa engSpaFromCursor(Cursor cursor) {
		int id = cursor.getInt(0);
		String english = cursor.getString(1);
		String spanish = cursor.getString(2);
		String wordTypeStr = cursor.getString(3);
		String qualifierStr = cursor.getString(4);
		String attributeStr = cursor.getString(5);
		WordType wordType = WordType.valueOf(wordTypeStr);
		Qualifier qualifier = Qualifier.valueOf(qualifierStr);
		Topic topic = Topic.valueOf(attributeStr);
		int level = cursor.getInt(6);
		return new EngSpa(id, english, spanish,
				wordType, qualifier, topic, level);
	}
	
	@Override // EngSpaDAO
	public EngSpa getRandomPassedWord(int userLevel) {
		if (userLevel < 2) return null;
		int max = (userLevel - 1) * EngSpaQuiz.WORDS_PER_LEVEL;
		int dbSize = getDictionarySize();
		if (max > dbSize) max = dbSize;
		int id = random.nextInt(max) + 1; // id starts from 1
		return getWordById(id);
	}
	@Override // EngSpaDAO
	public EngSpa getWordById(int id) {
		if (BuildConfig.DEBUG) Log.d(TAG, "getWordById(" + id + ")");
		Cursor cursor = null;
		try {
			cursor = getCursor(PROJECTION_ALL_FIELDS,
					BaseColumns._ID + "=?", // selection
					new String[] { Integer.toString(id)}, // selectionArgs
					null, // groupBy
					null, // having,
					null); // orderBy

			if (cursor.moveToFirst()) {
				return engSpaFromCursor(cursor);
			} else {
				Log.w(TAG, "getWordById; moveToFirst() was false!");
				return null;
			}
		} finally {
			if (cursor != null) cursor.close();
		}
	}

	@Override // EngSpaDAO
	public List<EngSpa> getSpanishWord(String spanish) {
		Cursor cursor = null;
		try {
			cursor = getCursor(PROJECTION_ALL_FIELDS,
					SPANISH + "=?", // selection
					new String[] { spanish}, // selectionArgs
					null, // groupBy
					null, // having,
					null); // orderBy
			List<EngSpa> matchList = new ArrayList<EngSpa>();
			while (cursor.moveToNext()) {
				matchList.add(engSpaFromCursor(cursor));
			}
			return matchList;
		} finally {
			if (cursor != null) cursor.close();
		}
	}

	@Override // EngSpaDAO
	public List<EngSpa> getEnglishWord(String english) {
		Cursor cursor = null;
		try {
			cursor = getCursor(PROJECTION_ALL_FIELDS,
					ENGLISH + "=?", // selection
					new String[] { english }, // selectionArgs
					null, // groupBy
					null, // having,
					null); // orderBy
			List<EngSpa> matchList = new ArrayList<EngSpa>();
			while (cursor.moveToNext()) {
				matchList.add(engSpaFromCursor(cursor));
			}
			return matchList;
		} finally {
			if (cursor != null) cursor.close();
		}
	}

	@Override // EngSpaDAO
	public List<EngSpa> findWords(EngSpa engSpa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override // EngSpaDAO
	public List<EngSpa> findWordsByTopic(String topic) {
		Cursor cursor = null;
		String field = ATTRIBUTE;
		if (topic.equals("phrase")) field = WORD_TYPE;
		try {
			cursor = getCursor(PROJECTION_ALL_FIELDS,
					field + "=?", // selection
					new String[] { topic }, // selectionArgs
					null, // groupBy
					null, // having,
					null); // orderBy
			List<EngSpa> matchList = new ArrayList<EngSpa>();
			while (cursor.moveToNext()) {
				matchList.add(engSpaFromCursor(cursor));
			}
			return matchList;
		} finally {
			if (cursor != null) cursor.close();
		}
		
	}

	@Override // EngSpaDAO
	public int getDictionarySize() {
		if (this.dictionarySize == 0) {
			this.dictionarySize = (int)
				DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE);
		}
		return this.dictionarySize;
	}
	@Override // EngSpaDAO
	public int getMaxUserLevel() {
		return getDictionarySize() / EngSpaQuiz.WORDS_PER_LEVEL;
	}
}
