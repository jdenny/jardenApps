package com.jardenconsulting.androidcourse;

import jarden.LayoutsFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class LayoutsActivity extends AppCompatActivity {
	private final static String TAG = "LayoutsActivity";
	private LayoutsFragment layoutsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layouts);
		this.layoutsFragment = new LayoutsFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, this.layoutsFragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.layouts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.linear) {
			this.layoutsFragment.setLayoutId(R.layout.layouts_linear);
		} else if (id == R.id.table) {
			this.layoutsFragment.setLayoutId(R.layout.layouts_table);
		} else if (id == R.id.relative) {
			this.layoutsFragment.setLayoutId(R.layout.layouts_relative);
		} else {
			Log.w(TAG, "unrecognised option:" + item);
			return super.onOptionsItemSelected(item);
		}
		forceRecreateView();
		return true;
	}

	public void forceRecreateView() {
		// force the fragment to re-create its view:
		getSupportFragmentManager().beginTransaction()
			.detach(this.layoutsFragment)
			.attach(this.layoutsFragment)
			.commit();
	}

}
