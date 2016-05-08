package jarden.provider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jarden.engspa.EngSpaSQLite2;
import jarden.engspa.EngSpaUtils;
import jarden.provider.engspa.EngSpaContract;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.util.Log;

import com.jardenconsulting.jardenprovider.R;

/*
 * syntax of Uri:
 * <scheme name> : // <authority>/[<path>]/<? <query>] [ # <fragment> ]
 * e.g. content://jarden.consulting.com/user/5
 * getPath() -> /user/5
 */
public class EngSpaProvider extends ContentProvider {
    private static final String TAG = "EngSpaProvider";
	private EngSpaSQLite2 engSpaSQLite;

	private static final String SELECT_BY_KEY = BaseColumns._ID + "=?";
	private String selection;
	private String[] selectionArgs;
	
	public EngSpaProvider() {
	}

	@Override // ContentProvider
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		setSelection(uri, selection, selectionArgs);
        checkPath(uri);
        int rows = this.engSpaSQLite.delete(this.selection, this.selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
	}
    private void checkPath(Uri uri) {
        String path = uri.getPath();
        if (!path.startsWith("/" + EngSpaContract.TABLE)) {
            throw new IllegalStateException("Only EngSpa table supported!");
        }
    }

	@Override // ContentProvider
	public String getType(Uri uri) {
		return null;
	}

	@Override // ContentProvider
	public boolean onCreate() {
		this.engSpaSQLite = EngSpaSQLite2.getInstance(getContext());
        if (engSpaSQLite.getDictionarySize() == 0) {
            try {
                InputStream is = getContext().getResources().openRawResource(
                        R.raw.engspatest);
                ContentValues[] contentValuesArray = EngSpaUtils.getContentValuesArray(is);
                engSpaSQLite.bulkInsert(contentValuesArray);
            } catch (IOException e) {
                Log.e(TAG, "exception in populateDatabase(): " + e);
            }
        }
		return true;
	}

	@Override // ContentProvider
	public Uri insert(Uri uri, ContentValues values) {
        checkPath(uri);
		Uri uri2;
        long newId = this.engSpaSQLite.insert(values);
        uri2 = Uri.parse(EngSpaContract.CONTENT_URI_STR + "/" + newId);
		getContext().getContentResolver().notifyChange(uri, null);
		return uri2;
	}

	@Override // ContentProvider
	public Cursor query(Uri uri, String[] columns, String selection,
			String[] selectionArgs, String sortOrder) {
		setSelection(uri, selection, selectionArgs);
        checkPath(uri);
		Cursor cursor = this.engSpaSQLite.getCursor(
                columns, this.selection,
                this.selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override // ContentProvider
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
        checkPath(uri);
		setSelection(uri, selection, selectionArgs);
		int rows = this.engSpaSQLite.update(values, this.selection, this.selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return rows;
	}

	@Override // ContentProvider
	public int bulkInsert(Uri uri, ContentValues[] values) {
        checkPath(uri);
		int rows = this.engSpaSQLite.bulkInsert(values);
		getContext().getContentResolver().notifyChange(uri, null);
		return rows;
	}

	@Override // ContentProvider
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		return super.openFile(uri, mode);
	}

	// Convenience method; it allows clients to append id to uri,
	// instead of setting up selection and selectionArgs.
	private void setSelection(Uri uri, String selection, String[] selectionArgs) {
		String key = getKeyFromUri(uri);
		if (key != null) {
			this.selection = SELECT_BY_KEY; 
			this.selectionArgs = new String[] {key};
		} else {
			this.selection = selection;
			this.selectionArgs = selectionArgs;
		}
	}
	private static String getKeyFromUri(Uri uri) {
		String path = uri.getPath();
		if (path != null) {
			String[] tokens = path.split("/");
			if (tokens.length >= 3) {
				return tokens[2];
			}
		}
		return null;
	}
		
}
