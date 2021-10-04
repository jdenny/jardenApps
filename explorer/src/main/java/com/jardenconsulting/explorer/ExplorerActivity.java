package com.jardenconsulting.explorer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

import jarden.explorer.ExplorerFragment;
import jarden.explorer.ImageFragment;
import jarden.explorer.MediaFragment;
import jarden.explorer.OnFileSelectedListener;
import jarden.explorer.SearchDialog;
import jarden.explorer.SearchService;
import jarden.explorer.TextFileFragment;

/* TODO:
resume playback after screen rotation
have option to sort by name, date, size etc
task bar:
	exit from explorerActivity
	home - return to "/"
can the search be like the one in developer.android.com, i.e. when hover over icon,
a search editText appears?
another editText field as auto-complete?
Possible enhancements: delete, move, rename; use SearchView; full screen
 */

/*
 * NOTES
When a pending notification starts ExplorerActivity, it is actually a new one.
To demonstrate, do a search, close Explorer, then click the notification.
This explains why the search list works; the back button is closing the
new Explorer, and exposing the original one.

There are some differences between this app and the one built in AndroidCourse.

Here, each Fragment is defined in activity_explorer.xml by a fragment element,
which means that the fragments are created as part of the setContentView() in
onCreate().

In the course, the fragment is added to a FrameLayout, and is not
actually created until ExplorerActivity.onCreate() has finished. That's why,
in ExplorerFragment we save away the fileNames passed from the notification
(as at this point the ArrayAdapter hasn't been created), and then in
ExplorerFragment.onCreateView() we have to check if fileNames has been set.

 */
public class ExplorerActivity extends AppCompatActivity
implements DialogInterface.OnClickListener, OnFileSelectedListener  {
	public static final String TAG = "ExplorerApp";
	public static final String FILE_NAMES_TAG = "fileNames";
	public static final String DIRECTORY_TAG = "directory";
	public static final String SEARCH_EXPR_TAG = "searchExpr";

	private static final String FRAGMENT_INDEX_TAG = "currentFragmentIndex";
	private static final String SEARCH_DIALOG = "jarden.explorer.SearchDialog";

	private FragmentManager fragmentManager;
	private ExplorerFragment explorerFragment;
	private TextFileFragment textFragment;
	private MediaFragment videoFragment;
	private ImageFragment imageFragment;
	private Fragment[] fragments;
	private enum FragmentNameEnum {
		EXPLORER, TEXT, VIDEO, IMAGE
	}
	private FragmentNameEnum currentFragmentNameEnum;
	private SearchDialog searchDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "ExplorerActivity.onCreate(savedInstanceState" +
					(savedInstanceState==null?"":"!") + "=null)");
		}
		setContentView(R.layout.activity_explorer);
		this.fragmentManager = getSupportFragmentManager();
		this.explorerFragment = (ExplorerFragment)
				this.fragmentManager.findFragmentById(R.id.explorerFragment);
		this.textFragment = (TextFileFragment)
				this.fragmentManager.findFragmentById(R.id.textFragment);
		this.videoFragment = (MediaFragment)
				this.fragmentManager.findFragmentById(R.id.videoFragment);
		this.imageFragment = (ImageFragment)
				this.fragmentManager.findFragmentById(R.id.imageFragment);
		// Note: fragments need to be in same order as FragmentNameEnum
		this.fragments = new Fragment[] {
				explorerFragment, textFragment,
				videoFragment, imageFragment
		};
		Intent intent = getIntent();
		String[] fileNames = intent.getStringArrayExtra(FILE_NAMES_TAG);
		if (fileNames != null) { // intent from Notification
			String directory = intent.getStringExtra(DIRECTORY_TAG);
			String searchExpr = intent.getStringExtra(SEARCH_EXPR_TAG);
			setTitle(directory + " (" + searchExpr + ")");
			explorerFragment.showFileNames(fileNames);
		}
		
		// default, if no savedInstanceState:
		this.currentFragmentNameEnum = FragmentNameEnum.EXPLORER;
		if (savedInstanceState != null) {
			int fragIndex = savedInstanceState.getInt(FRAGMENT_INDEX_TAG);
			this.currentFragmentNameEnum = FragmentNameEnum.values()[fragIndex];
			if (BuildConfig.DEBUG) Log.d(TAG, "restoring to fragment: " +
					this.currentFragmentNameEnum);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		showFragment(this.currentFragmentNameEnum);
	}
	@Override
	public boolean onSupportNavigateUp() {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "ExplorerActivity.onSupportNavigateUp()");
			this.explorerFragment.showRootDirectory();
		}
		return super.onSupportNavigateUp();
	}
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(FRAGMENT_INDEX_TAG, this.currentFragmentNameEnum.ordinal());
        super.onSaveInstanceState(savedInstanceState);
    }
	private void showFragment(FragmentNameEnum fragmentIndexEnum) {
		int orientation = getResources().getConfiguration().orientation;
		Fragment fragment = fragments[fragmentIndexEnum.ordinal()];
		FragmentTransaction ft = fragmentManager.beginTransaction();
		for (Fragment frag: fragments) {
			if (orientation == Configuration.ORIENTATION_LANDSCAPE
					&& frag == this.explorerFragment) {
				// don't do anything, i.e. never hide explorerFragment
			} else {
				if (frag == fragment) {
					ft.show(frag);
				} else {
					ft.hide(frag);
				}
			}
		}
		this.currentFragmentNameEnum = fragmentIndexEnum;
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.explorer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.actionSearch) {
			showSearchDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showSearchDialog() {
		FragmentManager fragMan = getSupportFragmentManager();
		this.searchDialog = (SearchDialog) fragMan.findFragmentByTag(SEARCH_DIALOG);
		if (this.searchDialog == null) {
			this.searchDialog = new SearchDialog();
		}
		this.searchDialog.show(fragMan, SEARCH_DIALOG);
	}

	@Override // button pressed in SearchDialog
	public void onClick(DialogInterface dialog, int id) {
		if (id == DialogInterface.BUTTON_POSITIVE) {
			if (BuildConfig.DEBUG) Log.d(TAG, "search.ok");
			String searchExpr = searchDialog.getSearchExpr();
			Intent searchIntent = new Intent(this, SearchService.class);
			searchIntent.putExtra(DIRECTORY_TAG,
					this.explorerFragment.getCurrentDirectory().getAbsolutePath());
			searchIntent.putExtra(SEARCH_EXPR_TAG, searchExpr);
			startService(searchIntent);
			Toast.makeText(this, "search running in background", Toast.LENGTH_LONG).show();
			
			// TODO: show file information, e.g. permissions
			// (or at least executable/directory/file), size, path
			// so for this, will need to pass FileList, not String[]
			//! this.explorerFragment.showFiles(fileNames);
		} else if (id == DialogInterface.BUTTON_NEGATIVE) {
			if (BuildConfig.DEBUG) Log.d(TAG, "search.cancel");
		}
	}


	@Override
	public void onBackPressed() {
		if (this.currentFragmentNameEnum == FragmentNameEnum.EXPLORER) {
			if (!explorerFragment.onBackPressed()) {
				super.onBackPressed();
			}
		} else {
			this.fragments[this.currentFragmentNameEnum.ordinal()].onPause();
			showFragment(FragmentNameEnum.EXPLORER);
		}
	}

	@Override
	public void onFileSelected(File file) {
		String fileName = file.getName();
		if (fileName.endsWith(".txt")) {
			textFragment.setFile(file);
			showFragment(FragmentNameEnum.TEXT);
		} else if (fileName.endsWith(".mp3") || fileName.endsWith(".mp4")) {
			videoFragment.setFile(file);
			showFragment(FragmentNameEnum.VIDEO);
		} else if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
			imageFragment.setFile(file);
			showFragment(FragmentNameEnum.IMAGE);
		}
	}

}
