package jarden.engspa;

import jarden.provider.engspa.EngSpaContract;
import jarden.provider.engspa.EngSpaContract.Topic;
import jarden.provider.engspa.EngSpaContract.Qualifier;
import jarden.provider.engspa.EngSpaContract.WordType;

import com.jardenconsulting.jardenprovider.BuildConfig;
import com.jardenconsulting.jardenprovider.MainActivity;
import com.jardenconsulting.jardenprovider.R;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class DetailFragment extends Fragment implements OnClickListener {
	private final static String WORD_ID_NAME = "WORD_ID_NAME";
	private EditText englishEdit;
	private EditText spanishEdit;
	private EditText levelEdit;
	private Spinner wordTypeSpinner;
	private Spinner qualifierSpinner;
	private Spinner attributeSpinner;
	private String wordId;
	private Uri wordUri;
	private View view; // Main Layout View

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "DetailFragment.onCreateView()");
		if (savedInstanceState != null) {
			this.wordId = savedInstanceState.getString(WORD_ID_NAME);
		}
		view = inflater.inflate(R.layout.eng_spa_edit_layout, container, false);
		Context context = view.getContext();
		englishEdit = (EditText) view.findViewById(R.id.english);
		spanishEdit = (EditText) view.findViewById(R.id.spanish);
		levelEdit = (EditText) view.findViewById(R.id.level);

		// now for the spinners:
		wordTypeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
		ArrayAdapter<String> wordTypeAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, EngSpaContract.wordTypeNames);
		wordTypeSpinner.setAdapter(wordTypeAdapter);
		
		qualifierSpinner = (Spinner) view.findViewById(R.id.qualifierSpinner);
		ArrayAdapter<String> qualifierAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, EngSpaContract.qualifierNames);
		qualifierSpinner.setAdapter(qualifierAdapter);
		
		attributeSpinner = (Spinner) view.findViewById(R.id.attributeSpinner);
		ArrayAdapter<String> attributeAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, EngSpaContract.attributeNames);
		this.attributeSpinner.setAdapter(attributeAdapter);
		// rest of views:
		Button newButton = (Button) view.findViewById(R.id.newButton);
		newButton.setOnClickListener(this);
		Button updateButton = (Button) view.findViewById(R.id.updateButton);
		updateButton.setOnClickListener(this);
		Button deleteButton = (Button) view.findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(this);
		return view;
	}
	@Override
	public void onResume() {
		super.onResume();
		if (BuildConfig.DEBUG) {
			Log.d(MainActivity.TAG,
				"DetailFragment.onResume(); wordId=" + this.wordId);
		}
		/*
		 * This code is put here because of the following sequence of events
		 * triggered when user selects a word from MasterFragment:
		 * 		masterFragment passes selected word back to mainActivity
		 * 		mainActivity calls detailFragment.setWordId()
		 * 		android calls detailFragment.onCreateView() and restores UI fields
		 * 			to values set when detailFragment was previously shown; these are old!
		 * 		android calls detailFragment.onResume(), which sets UI fields to correct values
		 */
		if (this.wordId != null) {
			showWord();
		}
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		ContentResolver contentResolver = getActivity().getContentResolver();
		String message;
		if (viewId == R.id.newButton) {
			Uri uri = contentResolver.insert(this.wordUri, getContentValues());
			message = "row inserted: " + uri.getPath();
		} else if (viewId == R.id.updateButton) {
			int rows = contentResolver.update(this.wordUri, getContentValues(), null, null);
			message = rows + " row updated";
		} else if (viewId == R.id.deleteButton) {
			int rows = contentResolver.delete(this.wordUri, null, null);
			message = rows + " row deleted";
		} else {
			message = "onClick(), unrecognised viewId: " + viewId;
		}
		setStatus(message);
	}
	private void setStatus(String message) {
		MainActivity mainActivity = (MainActivity) getActivity();
		mainActivity.setStatus(message);
	}
	
	// rather irregular to pass mainActivity, but getActivity might not yet
	// work, as Fragment hasn't been fully initialised
	public void setWordId(String wordId, MainActivity mainActivity) {
		if (BuildConfig.DEBUG) {
			Log.d(MainActivity.TAG, "DetailFragment.setWordId(" +
					wordId + ")");
		}
		this.wordId = wordId;
		String uriStr = EngSpaContract.CONTENT_URI_STR + "/" + this.wordId;
		this.wordUri = Uri.parse(uriStr);
	}
	private void showWord() {
		String[] selectionArgs = null;
		String sortOrder = null;
		Cursor cursor = getActivity().getContentResolver().query(
				// this.wordUri, EngSpaContract.PROJECTION_ALL_FIELDS, selection,
				EngSpaContract.CONTENT_URI_ENGSPA, EngSpaContract.PROJECTION_ALL_FIELDS,
				BaseColumns._ID + "=" + this.wordId,
				selectionArgs, sortOrder);
		if (cursor.moveToFirst()) {
			String eng = cursor.getString(1);
			String spa = cursor.getString(2);
			this.englishEdit.setText(eng);
			this.spanishEdit.setText(spa);
			if (BuildConfig.DEBUG) {
				Log.d(MainActivity.TAG,
						"DetailFragment.showWord(); eng=" + eng + ", spa=" + spa);
			}
			String wordType = cursor.getString(3);
			int position = WordType.valueOf(wordType).ordinal();
			this.wordTypeSpinner.setSelection(position);

			String qualifier = cursor.getString(4);
			position = Qualifier.valueOf(qualifier).ordinal();
			this.qualifierSpinner.setSelection(position);
			
			String attribute = cursor.getString(5);
			position = Topic.valueOf(attribute).ordinal();
			this.attributeSpinner.setSelection(position);
			
			String level = cursor.getString(6);
			this.levelEdit.setText(level);
			setStatus("");
		} else {
			setStatus("no matching word found!");
		}
	}
	
	private ContentValues getContentValues() {
		String wordType = (String) wordTypeSpinner.getSelectedItem();
		String qualifier = (String) qualifierSpinner.getSelectedItem();
		String attribute = (String) attributeSpinner.getSelectedItem();
		Integer level = Integer.valueOf(levelEdit.getText().toString());
		ContentValues values = new ContentValues();
		values.put(EngSpaContract.ENGLISH, englishEdit.getText().toString());
		values.put(EngSpaContract.SPANISH, spanishEdit.getText().toString());
		values.put(EngSpaContract.WORD_TYPE, wordType);
		values.put(EngSpaContract.QUALIFIER, qualifier);
		values.put(EngSpaContract.ATTRIBUTE, attribute);
		values.put(EngSpaContract.LEVEL, level);
		return values;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
        if(BuildConfig.DEBUG) {
        	Log.d(MainActivity.TAG, "DetailFragment.onSaveInstanceState(" +
        			(outState==null?"null":"not null") +")");
        }
		super.onSaveInstanceState(outState);
		outState.putString(WORD_ID_NAME, this.wordId);
	}

}
