package com.jardenconsulting.spanishapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jarden.app.race.RaceFragment;
import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaQuiz;
import jarden.engspa.EngSpaSQLite2;
import jarden.engspa.EngSpaUser;
import jarden.engspa.EngSpaUtils;
import jarden.http.MyHttpClient;
import jarden.provider.engspa.EngSpaContract.QAStyle;
import jarden.quiz.QuizCache;

import com.jardenconsulting.spanishapp.UserDialog.UserSettingsListener;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
		implements EngSpaActivity, UserSettingsListener,
		TopicDialog.TopicListener, QAStyleDialog.QAStyleListener,
		ListView.OnItemClickListener {
    public static final String TAG = "SpanishMain";
	
    private static final String TITLE_KEY = "title";
    private static final String ENGSPA_TXT_VERSION_KEY = "EngSpaTxtVersion";
    private static final String UPDATES_VERSION_KEY = "DataVersion";
    private static final String ENG_SPA_UPDATES_NAME = 
    		QuizCache.serverUrlStr + "engspaupdates.txt?attredirects=0&d=1";
    private static final String CURRENT_FRAGMENT_TAG =
    		"currentFragmentTag";
    private static String questionSequenceKey = null;
	private static final String WORD_SEARCH = "WORD_SEARCH";
	private static final String NUMBER_GAME = "NUMBER_GAME";
	private static final String ENGSPA = "ENGSPA";
	private EngSpaDAO engSpaDAO;
	private EngSpaFragment engSpaFragment;
	private VerbTableFragment verbTableFragment;
	private RaceFragment raceFragment;
	private Fragment currentFragment;
	private String currentFragmentTag;
	private DialogFragment userDialog;
	private HelpDialog helpDialog;
	private TopicDialog topicDialog;
	private QAStyleDialog qaStyleDialog;
	private ProgressBar progressBar;
	private TextView statusTextView;
	private boolean engSpaFileModified;
	private long dateEngSpaFileModified;
	private SharedPreferences sharedPreferences;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private String[] drawerTitles;
	private String engSpaTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) Log.d(TAG,
				"MainActivity.onCreate(savedInstanceState is " +
				(savedInstanceState==null?"":"not ") + "null)");
		getEngSpaDAO();
		this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
		setContentView(R.layout.activity_main);
		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolBar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.statusTextView = (TextView) findViewById(R.id.statusTextView);
		this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.drawerList = (ListView) findViewById(R.id.left_drawer);
		Resources resources = getResources();
		this.drawerTitles = resources.getStringArray(R.array.navigationDrawerTitles);
		TypedArray iconArray = resources.obtainTypedArray(R.array.navigationDrawIcons);
		int drawerTitlesLength = drawerTitles.length;
		DrawerItem[] drawerItems = new DrawerItem[drawerTitlesLength];
		for (int i = 0; i < drawerTitlesLength; i++) {
			drawerItems[i] = new DrawerItem(iconArray.getDrawable(i), drawerTitles[i]);
		}
		iconArray.recycle();
		DrawerItemAdapter adapter = new DrawerItemAdapter(this,
				R.layout.drawer_list_item, drawerItems);
		this.drawerList.setAdapter(adapter);
        this.drawerList.setOnItemClickListener(this);
        
		if (savedInstanceState == null) {
			this.currentFragmentTag = ENGSPA;
		} else {
			this.currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
			if (this.currentFragmentTag == null) this.currentFragmentTag = ENGSPA;
			FragmentManager fragmentManager = getSupportFragmentManager();
			this.engSpaFragment = (EngSpaFragment) fragmentManager.findFragmentByTag(ENGSPA);
			this.verbTableFragment = (VerbTableFragment) fragmentManager.findFragmentByTag(WORD_SEARCH);
			this.raceFragment = (RaceFragment) fragmentManager.findFragmentByTag(NUMBER_GAME);
			String title = savedInstanceState.getString(TITLE_KEY);
			if (title != null) setTitle(title);
		}
		loadDB();
	}
	/*
	 * use engspaversion.txt and sharedPreferences to see if there
	 * is a new version of local resource file engspa.txt, and if
	 * so, reload the database from engspa.txt 
	 */
	private void loadDB() {
		InputStream is = getResources().openRawResource(R.raw.engspaversion);
		List<String> engSpaVersionLines;
		try {
			engSpaVersionLines = EngSpaUtils.getLinesFromStream(is);
			final int version = Integer.parseInt(engSpaVersionLines.get(0));
			final SharedPreferences sharedPreferences = getSharedPreferences();
			int savedVersion = sharedPreferences.getInt(ENGSPA_TXT_VERSION_KEY, 0);
			if (version <= savedVersion) {
				dbLoadComplete();
			} else {
				String statusMessage =
					(savedVersion == 0)?"Loading Spanish dictionary":
					"Loading new dictionary version";
				setStatus(statusMessage + "; please be patient with us!");
				setProgressBarVisible(true);
				new Thread(new Runnable() {
					private String threadResult;
					@Override
					public void run() {
						threadResult = "rows inserted into database: " +
								engSpaDAO.newDictionary();
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putInt(ENGSPA_TXT_VERSION_KEY, version);
						editor.commit();
						runOnUiThread(new Runnable() {
							public void run() {
								setStatus(threadResult);
								setProgressBarVisible(false);
								dbLoadComplete();
							}
						});
					}
				}).start();
			}
		} catch (IOException e) {
			Log.e(getTag(), "MainActivity.loadDB(): " + e);
			setStatus("error loading database: " + e);
		}
	}
	private void dbLoadComplete() {
		showFragment();
	}
	@Override // Activity
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override // OnItemClickListener - for DrawerList
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 0) {
			if (this.qaStyleDialog == null) this.qaStyleDialog = new QAStyleDialog();
			this.qaStyleDialog.show(getSupportFragmentManager(), "QAStyleDialog");
		} else if (position == 1) {
			showTopicDialog();
		} else if (position == 2) {
			showFragment(WORD_SEARCH);
			setTitle(drawerTitles[position]);
		} else if (position == 3) {
			showFragment(NUMBER_GAME);
			setTitle(drawerTitles[position]);
		} else if (position == 4) {
			this.engSpaFragment.setTopic(null);
			showFragment(ENGSPA);
		} else if (position == 5) {
			if (this.helpDialog == null) this.helpDialog = new HelpDialog();
			this.helpDialog.show(getSupportFragmentManager(), "HelpDialog");
		} else {
			this.statusTextView.setText("unrecognised item position: " + position);
		}
		this.drawerList.setItemChecked(position, true);
		this.drawerList.setSelection(position);
		this.drawerLayout.closeDrawer(this.drawerList);
    }	
	@Override // Activity
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (BuildConfig.DEBUG) Log.d(TAG,
				"MainActivity.onOptionsItemSelected(itemId=" + id);
		if (id == android.R.id.home) {
			drawerLayout.openDrawer(drawerList);
			return true;
		} else if (id == R.id.userSettings) {
			if (this.userDialog == null) this.userDialog = new UserDialog();
			this.userDialog.show(getSupportFragmentManager(), "UserSettingsDialog");
			return true;
		} else if (id == R.id.speakerButton) {
			this.engSpaFragment.speakSpanish(this.currentFragmentTag.equals(ENGSPA));
			return true;
		} else if (id == R.id.helpButton) {
			this.statusTextView.setText(R.string.tipTip);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override // Activity
	public void onBackPressed() {
		super.onBackPressed();
		if (!this.currentFragmentTag.equals(ENGSPA)) {
			currentFragmentTag = ENGSPA;
			currentFragment = this.engSpaFragment;
			setTitle(this.engSpaTitle);
		}
		/* TODO: what happens here?
		if (drawerLayout.isDrawerOpen(drawerList)) {
			super.onBackPressed();
		} */
	}

	@Override // EngSpaActivity
	public void showTopicDialog() {
		if (this.topicDialog == null) this.topicDialog = new TopicDialog();
		this.topicDialog.show(getSupportFragmentManager(), "TopicDialog");
	}
	@Override // TopicDialog.TopicListener
	public void onTopicSelected(String topic) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"MainActivity.onTopicSelected(" + topic + ")");
		this.engSpaFragment.setTopic(topic);
	}
	@Override // QAStyleDialog.QAStyleListener
	public void onQAStyleSelected(QAStyle qaStyle) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"MainActivity.onQAStyleSelected(" + qaStyle + ")");
		this.engSpaFragment.setUserQAStyle(qaStyle);
	}
	@Override // Activity
	public void onSaveInstanceState(Bundle savedInstanceState) {
		CharSequence title = getTitle();
		if (BuildConfig.DEBUG) Log.d(TAG,
				"MainActivity.onSaveInstanceState(); title=" + title);
		if (title != null) {
			savedInstanceState.putString(TITLE_KEY, title.toString());
		}
		if (this.currentFragment != null) {
			savedInstanceState.putString(CURRENT_FRAGMENT_TAG,
					this.currentFragmentTag);
		}
		super.onSaveInstanceState(savedInstanceState);
	}
	/**
	 * Update EngSpa table on database if there is a new version of
	 * engspaupdates.txt on server. Version determined from
	 * dateLastModified, which is saved in SharedPreferences.
	 */
	public void checkForDBUpdates() {
		engSpaFileModified = false;
		this.statusTextView.setText("checking for updates...");
		new Thread(new Runnable() {
			private String statusMessage = "";
			@Override
			public void run() {
				long savedVersion = sharedPreferences.getLong(UPDATES_VERSION_KEY, 0);
				try {
					String urlStr = ENG_SPA_UPDATES_NAME + "?attredirects=0&d=1";
					dateEngSpaFileModified = MyHttpClient.getLastModified(urlStr);
					engSpaFileModified = dateEngSpaFileModified > savedVersion;
					if (engSpaFileModified) {
						List<String> engSpaLines = MyHttpClient.getPageLines(
								ENG_SPA_UPDATES_NAME + "?attredirects=0&d=1", "iso-8859-1");
						engSpaDAO.updateDictionary(engSpaLines);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putLong(UPDATES_VERSION_KEY, dateEngSpaFileModified);
						editor.commit();
						statusMessage = "dictionary updated";
					} else {
						statusMessage = "dictionary up to date";
					}
				} catch (IOException e) {
					statusMessage = "error checking for updates: " + e;
					Log.e(TAG, "Exception in checkDataFileVersion: " + e);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						statusTextView.setText(statusMessage);
					}
				});
			}
		}).start();
	}
	
	@Override
	public EngSpaQuiz getEngSpaQuiz() {
		return this.engSpaFragment.getEngSpaQuiz();
	}
	private void showFragment(String fragmentTag) {
		if (this.currentFragmentTag.equals(fragmentTag)) {
			if (BuildConfig.DEBUG) Log.d(TAG,
					"MainActivity.showFragment(" + fragmentTag +
					"); already current fragment");
			return;
		}
		this.currentFragmentTag = fragmentTag;
		showFragment();
	}
	private void showFragment() {
		if (this.currentFragmentTag.equals(ENGSPA)) {
			if (this.engSpaFragment == null) {
				this.engSpaFragment = new EngSpaFragment();
			}
			this.currentFragment = engSpaFragment;
		} else if (this.currentFragmentTag.equals(WORD_SEARCH)) {
			if (this.verbTableFragment == null) {
				this.verbTableFragment = new VerbTableFragment();
			}
			this.currentFragment = verbTableFragment;
		} else if (this.currentFragmentTag.equals(NUMBER_GAME)) {
			if (this.raceFragment == null) {
				this.raceFragment = new RaceFragment();
			}
			this.currentFragment = raceFragment;
		}
		FragmentManager manager = getSupportFragmentManager();
		// pop backstack if there is anything to pop;
		// in case user chooses fragments from drawer without
		// pressing 'back'
		boolean popped = manager.popBackStackImmediate();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "MainActivity.showFragment(); popped=" + popped);
		}
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.fragmentLayout, currentFragment, currentFragmentTag);
		if (currentFragmentTag.equals(WORD_SEARCH) ||
				currentFragmentTag.equals(NUMBER_GAME)) {
			transaction.addToBackStack(currentFragmentTag);
		}
		transaction.commit();
	}

	/**
	 * Called on completion of UserDialog, which was called either
	 * because there is no EngSpaUser yet defined, or because the
	 * user chose to update it.
	 */
	@Override // UserSettingsListener
	public void onUpdateUser(String userName, int userLevel, QAStyle qaStyle) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"MainActivity.onUpdateUser(" + userName + ", " + userLevel +
				", " + qaStyle + ")");
		if (userName.length() < 1) {
			this.statusTextView.setText("no user name supplied");
			return;
		}
		if (userLevel < 1) {
			this.statusTextView.setText("invalid userLevel supplied");
			return;
		}
		this.engSpaFragment.setUser(userName, userLevel, qaStyle);
	}
	@Override // UserSettingsListener
	public EngSpaUser getEngSpaUser() {
		return this.engSpaFragment.getEngSpaUser();
	}
	
	@Override // EngSpaActivity
	public void setStatus(String status) {
		this.statusTextView.setText(status);
	}
	@Override // EngSpaActivity
	public String getTag() {
		return TAG;
	}
	/**
	 * questionSequence is a sequence number that is incremented
	 * each time a question is asked.
	 */
	@Override // EngSpaActivity
	public int getQuestionSequence() {
		if (questionSequenceKey == null) {
			questionSequenceKey = "QSN_" + getEngSpaUser().getUserId();
		}
		int questionSeq = sharedPreferences.getInt(questionSequenceKey, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(questionSequenceKey, ++questionSeq);
		editor.commit();
		return questionSeq;
	}
	@Override // EngSpaActivity
	public void setSpanish(String spanish) {
		// note: spanish String is held in engSpaFragment, so
		// it can survive screen rotation. 
		this.engSpaFragment.setSpanish(spanish);
	}
	@Override // EngSpaActivity
	public void speakSpanish(String spanish) {
		this.engSpaFragment.speakSpanish(spanish);
	}
	@Override // EngSpaActivity
	public void setEngSpaTitle(String title) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"MainActivity.setEngSpaTitle(" + title + ")");
		this.engSpaTitle = title;
		super.setTitle(title);
	}
	@Override // EngSpaActivity
	public void setProgressBarVisible(boolean visible) {
		progressBar.setVisibility(visible?ProgressBar.VISIBLE:ProgressBar.INVISIBLE);
	}
	@Override
	public SharedPreferences getSharedPreferences() {
		return this.sharedPreferences;
	}
	@Override
	public EngSpaDAO getEngSpaDAO() {
		if (this.engSpaDAO == null) {
			this.engSpaDAO = EngSpaSQLite2.getInstance(this, TAG);
		}
		return this.engSpaDAO;
	}
}
