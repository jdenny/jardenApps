package com.jardenconsulting.jardenprovider;

import jarden.engspa.DetailFragment;
import jarden.engspa.MasterFragment;
import jarden.provider.engspa.EngSpaContract;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

// TODO: use AsyncQueryHandler 
// handle alternative meanings, e.g. face = la cara or el rostro
// use query(distinct=true..) to get subtypes;
// selection criteria on masterFragment
// reload data when changed; isn't this what loader should do?
public class MainActivity extends FragmentActivity {
	public final static String TAG = "jardenProviders";
	public final static int WORD_LOADER_ID = 1;
	public final static int USER_LOADER_ID = 2;
	private FragmentManager fragmentManager;
	private MasterFragment masterFragment;
	private DetailFragment detailFragment;
	private TextView statusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.statusView = (TextView) findViewById(R.id.statusView);
		this.fragmentManager = getSupportFragmentManager();
		this.masterFragment = new MasterFragment();
		setFragment(masterFragment, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.wordTable) {
			setFragment(this.masterFragment, false);
			return true;
		} else if (id == R.id.restoreDB) {
			// Restore words dictionary from local text file. Used in case updateDB fails.
			// TODO: show pop-up asking for confirmation:
			// this will restore the database to its original values
			// use menu option Update Database to get latest version

			int rowCt = getContentResolver().bulkInsert(
					EngSpaContract.CONTENT_URI_ENGSPA,
					new ContentValues[0]); // zero-length array says revert to local file
			Log.i(MainActivity.TAG, "restoreDB(); rows inserted to database: " + rowCt);
			Toast.makeText(this, "database restored to original values",
					Toast.LENGTH_LONG).show();
			
		}
		return super.onOptionsItemSelected(item);
	}

	public void setStatus(String message) {
		this.statusView.setText(message);
	}
	private void setFragment(Fragment fragment, boolean addToBackStack) {
		FragmentTransaction ft = this.fragmentManager.beginTransaction();
		ft.replace(R.id.fragmentContainer, fragment);
		if (addToBackStack) ft.addToBackStack("detail");
		ft.commit();
	}
	
	public void setWordId(String wordId) {
		if (this.detailFragment == null) {
			detailFragment = new DetailFragment();
		}
		detailFragment.setWordId(wordId, this);
		setFragment(detailFragment, true);
	}
}
