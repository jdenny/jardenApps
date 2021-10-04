package jarden.engspa;

import jarden.provider.engspa.EngSpaContract;

import com.jardenconsulting.jardenprovider.MainActivity;
import com.jardenconsulting.jardenprovider.R;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MasterFragment extends Fragment
		implements OnClickListener, OnItemClickListener, LoaderCallbacks<Cursor> {
	private ListView listView;
	private MainActivity mainActivity;
	private SimpleCursorAdapter adapter;
	private String[] summaryColumns = {
			BaseColumns._ID, EngSpaContract.ENGLISH,
			EngSpaContract.SPANISH, EngSpaContract.WORD_TYPE
	};
	private int[] summaryViews = {R.id.wordId, R.id.wordEng, R.id.wordSpa, R.id.wordType};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.eng_spa_list_layout, container, false);
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		
		this.adapter = new SimpleCursorAdapter(mainActivity,
				R.layout.word_layout, null, summaryColumns, summaryViews, 0);
		this.listView.setAdapter(adapter);
		mainActivity.getSupportLoaderManager().initLoader(MainActivity.WORD_LOADER_ID, null, this);
		return view;
	}

	@Override
	public void onClick(View view) {
		/* TODO: add 2 spinners and Go button to enable user to get subset of db rows
		int viewId = view.getId();
		String message;
		if (viewId == R.id.goButton) {
			
		} else {
			message = "onClick(), unrecognised viewId: " + viewId;
		}
		this.statusText.setText(message);
		*/
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View selectedRowView,
			int position, long id) {
		TextView idView = (TextView) selectedRowView.findViewById(R.id.wordId);
		String idStr = idView.getText().toString();
		this.mainActivity.setWordId(idStr);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mainActivity = (MainActivity)activity;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loadId, Bundle args) {
		Uri uri = EngSpaContract.CONTENT_URI_ENGSPA;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		return new CursorLoader(mainActivity, uri, summaryColumns, selection,
				selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}
