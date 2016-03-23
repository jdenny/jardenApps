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

import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
		implements EngSpaActivity, UserSettingsListener,
		TopicDialog.TopicListener, QAStyleDialog.QAStyleListener,
		/*!!ListView.OnItemClickListener*/ NavigationView.OnNavigationItemSelectedListener,
        ListView.OnItemLongClickListener {

    private static final String TAG = "MainActivity";
	private static final String ENGSPA_TXT_VERSION_KEY = "EngSpaTxtVersion";
	private static final String UPDATES_VERSION_KEY = "DataVersion";
	private static final String ENG_SPA_UPDATES_NAME =
			QuizCache.serverUrlStr + "engspaupdates.txt?attredirects=0&d=1";
    // Fragment tags:
	private static final String CURRENT_FRAGMENT_TAG =
			"currentFragmentTag";
    private String previousFragmentTag;
	private static final String WORD_LOOKUP = "WORD_LOOKUP";
	private static final String NUMBER_GAME = "NUMBER_GAME";
	private static final String ENGSPA = "ENGSPA";
    private static final String HELP = "HELP";
    private static final String VIEWLESS = "VIEWLESS";
    // end of Fragment tags
    private static String questionSequenceKey = null;
	private EngSpaDAO engSpaDAO;
	private FragmentManager fragmentManager;
	private EngSpaFragment engSpaFragment;
	private WordLookupFragment wordLookupFragment;
	private RaceFragment raceFragment;
	private Fragment currentFragment;
    private HelpFragment helpFragment;
    private ViewlessFragment viewlessFragment;
	private String currentFragmentTag;
	private DialogFragment userDialog;
	private TopicDialog topicDialog;
	private QAStyleDialog qaStyleDialog;
	private ProgressBar progressBar;
	private TextView statusTextView;
	private boolean engSpaFileModified;
	private long dateEngSpaFileModified;
	private SharedPreferences sharedPreferences;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
    private TextView tipTextView;
	private boolean doubleBackToExitPressedOnce = false;
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
				(savedInstanceState==null?"":"not ") + "null)");
        super.onCreate(savedInstanceState);
		getEngSpaDAO();
		this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        this.actionBar = getSupportActionBar();
        /*!!
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        this.actionBar.setHomeButtonEnabled(true);
        */

		this.statusTextView = (TextView) findViewById(R.id.statusTextView);
        // TODO: put tipTextView into each fragment; maybe use include?
        this.tipTextView = (TextView) findViewById(R.id.tipTextView);
        boolean isShowTips = sharedPreferences.getBoolean(SHOW_TIPS_KEY, true);
        setShowTips(isShowTips);
		this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*!!
		this.drawerList = (ListView) findViewById(R.id.left_drawer);
        Resources resources = getResources();
        String[] drawerTitles = resources.getStringArray(R.array.navigationDrawerTitles);
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
		this.drawerList.setOnItemLongClickListener(this);
		*/
        this.drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

		this.fragmentManager = getSupportFragmentManager();
		if (savedInstanceState == null) {
            this.viewlessFragment = new ViewlessFragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(this.viewlessFragment, VIEWLESS);
            ft.commit();
            boolean isShowHelp = sharedPreferences.getBoolean(SHOW_HELP_KEY, true);
            if (isShowHelp) {
                this.currentFragmentTag = HELP;
                showFragment(); // user can read help while db is loading
            } else {
                this.currentFragmentTag = ENGSPA;
            }
            loadDB(); // which in turn -> dbLoadComplete() -> showFragment()
		} else {
			this.currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            this.viewlessFragment =
                    (ViewlessFragment) fragmentManager.findFragmentByTag(VIEWLESS);
			this.wordLookupFragment =
                    (WordLookupFragment) fragmentManager.findFragmentByTag(WORD_LOOKUP);
			this.raceFragment = (RaceFragment) fragmentManager.findFragmentByTag(NUMBER_GAME);
            this.engSpaFragment = (EngSpaFragment) fragmentManager.findFragmentByTag(ENGSPA);
            this.helpFragment = (HelpFragment) fragmentManager.findFragmentByTag(HELP);
            if (this.currentFragmentTag == null) this.currentFragmentTag = ENGSPA;
            showFragment();
		}
	}
    private void dbLoadComplete() {
        showFragment();
        checkForDBUpdates();
    }

    @Override // Activity
    public void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) Log.d(TAG, "onResume()");
    }
	@Override // EngSpaActivity
    public void setTip(int resId) {
        this.tipTextView.setText((resId));
    }
    @Override // EngSpaActivity
    public void setShowTips(boolean isShowTips) {
        int visibility = isShowTips ? View.VISIBLE : View.GONE;
        this.tipTextView.setVisibility(visibility);
        SharedPreferences.Editor editor =
                this.sharedPreferences.edit();
        editor.putBoolean(EngSpaActivity.SHOW_TIPS_KEY, isShowTips);
        editor.apply();
    }

	/*
	 * use engspaversion.txt and sharedPreferences to see if there
	 * is a new version of local resource file engspa.txt, and if
	 * so, reload the database from engspa.txt 
	 */
	private void loadDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = getResources().openRawResource(R.raw.engspaversion);
                List<String> engSpaVersionLines;
                try {
                    engSpaVersionLines = EngSpaUtils.getLinesFromStream(is);
                    final int version = Integer.parseInt(engSpaVersionLines.get(0));
                    final SharedPreferences sharedPreferences = getSharedPreferences();
                    final int savedVersion = sharedPreferences.getInt(ENGSPA_TXT_VERSION_KEY, 0);
                    final boolean newDictionary = version > savedVersion;
                    // report results so far:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!newDictionary) {
                                dbLoadComplete();
                            } else {
                                int statusId = (savedVersion == 0) ?
                                        R.string.loadingDB : R.string.reloadingDB;
                                setStatus(statusId);
                                setProgressBarVisible(true);
                            }
                        }
                    });
                    if (newDictionary) {
                        engSpaDAO.newDictionary();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(ENGSPA_TXT_VERSION_KEY, version);
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                setStatus(R.string.loadDBDone);
                                setProgressBarVisible(false);
                                dbLoadComplete();
                            }
                        });
                    }
                } catch (IOException e) {
                    Log.e(TAG, "loadDB(): " + e);
                    setStatus(R.string.errorLoadingDB);
                }
            }
        }).start();
	}
	@Override // Activity
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    /*!!
	@Override // OnItemClickListener - for DrawerList
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 0) {
			if (this.qaStyleDialog == null) this.qaStyleDialog = new QAStyleDialog();
			this.qaStyleDialog.show(getSupportFragmentManager(), "QAStyleDialog");
		} else if (position == 1) {
			showTopicDialog();
		} else if (position == 2) {
			showFragment(WORD_LOOKUP);
		} else if (position == 3) {
			showFragment(NUMBER_GAME);
		} else if (position == 4) {
            onTopicSelected(null);
        } else if (position == 5) {
            showFragment(HELP);
        } else if (position == 6) {
            super.onBackPressed();
            return;
		} else {
			Log.e(TAG, "unrecognised item position: " + position);
		}
		this.drawerList.setItemChecked(position, true);
		this.drawerList.setSelection(position);
        this.drawerLayout.closeDrawer(this.drawerList);
	}
	*/
    @Override // OnNavigationItemSelectedListener
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected(menuItem.id=" + id + ")");
        if (id == R.id.qaStyle) {
            if (this.qaStyleDialog == null) this.qaStyleDialog = new QAStyleDialog();
            this.qaStyleDialog.show(getSupportFragmentManager(), "QAStyleDialog");
        } else if (id == R.id.topic) {
            showTopicDialog();
        } else if (id == R.id.wordLookup) {
            showFragment(WORD_LOOKUP);
        } else if (id == R.id.numbersGame) {
            showFragment(NUMBER_GAME);
        } else if (id == R.id.qByLevel) {
            onTopicSelected(null);
        } else if (id == R.id.help) {
            showFragment(HELP);
        } else if (id == R.id.exit) {
            super.onBackPressed();
        } else {
            Log.e(TAG, "unrecognised drawer menu item id: " + id);
        }
        this.drawerLayout.closeDrawers();
        return true;
    }
	@Override // OnItemLongClickListener - for DrawerList
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Snackbar.make(view, "position=" + position + "; id=" + id, Snackbar.LENGTH_LONG).show();
		return false;
	}
	@Override // Activity
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (BuildConfig.DEBUG) Log.d(TAG,
				"onOptionsItemSelected(itemId=" + id);
		if (id == android.R.id.home) {
			drawerLayout.openDrawer(drawerList);
			return true;
		} else if (id == R.id.userSettings) {
			if (this.userDialog == null) this.userDialog = new UserDialog();
			this.userDialog.show(getSupportFragmentManager(), "UserSettingsDialog");
			return true;
		} else if (id == R.id.speakerButton) {
            boolean spoken = this.viewlessFragment.speakSpanish();
            if (!spoken) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "speakButton(); this.spanish==null");
                }
                this.setStatus(R.string.spanishNull);
            }
            return true;
		} else if (id == R.id.deleteAllFails) {
			this.engSpaFragment.getEngSpaQuiz().deleteAllFails();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override // Activity
	public void onBackPressed() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onBackPressed(); currentFragmentTag=" +
                this.currentFragmentTag);
        if (ENGSPA.equals(this.currentFragmentTag)) {
			if (doubleBackToExitPressedOnce) {
				super.onBackPressed();
				return;
			}
			this.doubleBackToExitPressedOnce = true;
			Snackbar.make(this.statusTextView, "Please click BACK again to exit",
					Snackbar.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
		} else {
            super.onBackPressed();
            // must be returning to EngSpaFragment as this is the only fragment
            // we ever put on the backStack
            this.currentFragmentTag = ENGSPA;
            this.currentFragment = this.engSpaFragment;
        }
	}

    @Override // EngSpaActivity
    public void showTopicDialog() {
		if (this.topicDialog == null) this.topicDialog = new TopicDialog();
        this.topicDialog.show(getSupportFragmentManager(), "TopicDialog");
	}

	@Override // TopicDialog.TopicListener
	public void onTopicSelected(String topic) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"onTopicSelected(" + topic + ")");
		lazyEngSpaFragment();
        this.engSpaFragment.setTopic(topic);
		showFragment(ENGSPA);
	}
	@Override // QAStyleDialog.QAStyleListener
	public void onQAStyleSelected(QAStyle qaStyle) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"onQAStyleSelected(" + qaStyle + ")");
        this.viewlessFragment.getEngSpaUser().setQAStyle(qaStyle);
		showFragment(ENGSPA);
	}
	@Override // Activity
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onSaveInstanceState(); currentFragmentTag=" +
					this.currentFragmentTag);
		}
		if (this.currentFragment != null) {
			savedInstanceState.putString(CURRENT_FRAGMENT_TAG,
					this.currentFragmentTag);
		}
		super.onSaveInstanceState(savedInstanceState);
	}
    private void lazyEngSpaFragment() {
        if (this.engSpaFragment == null) {
            this.engSpaFragment = new EngSpaFragment();
        }
    }
	/**
	 * Update EngSpa table on database if there is a new version of
	 * engspaupdates.txt on server. Version determined from
	 * dateLastModified, which is saved in SharedPreferences.
	 */
	private void checkForDBUpdates() {
		engSpaFileModified = false;
		// this.statusTextView.setText("checking for updates...");
		Toast.makeText(this, "checking for updates...", Toast.LENGTH_LONG).show();
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
						Toast.makeText(MainActivity.this, statusMessage,
								Toast.LENGTH_LONG).show();
					}
				});
			}
		}).start();
	}

	private void showFragment(String fragmentTag) {
		if (this.currentFragmentTag != null && fragmentTag.equals(this.currentFragmentTag)) {
			if (BuildConfig.DEBUG) Log.d(TAG,
					"showFragment(" + fragmentTag +
					"); already current fragment");
			return;
		}
        this.previousFragmentTag = this.currentFragmentTag;
		this.currentFragmentTag = fragmentTag;
		showFragment();
	}
    private void hideEngSpaFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(this.engSpaFragment);
        ft.commit();
    }
	private void showFragment() {
        if (this.currentFragmentTag.equals(ENGSPA)) {
            if (this.engSpaFragment == null) {
                this.engSpaFragment = new EngSpaFragment();
            }
            this.currentFragment = engSpaFragment;
        } else if (this.currentFragmentTag.equals(WORD_LOOKUP)) {
            if (this.wordLookupFragment == null) {
                this.wordLookupFragment = new WordLookupFragment();
            }
            this.currentFragment = wordLookupFragment;
        } else if (this.currentFragmentTag.equals(NUMBER_GAME)) {
            if (this.raceFragment == null) {
                this.raceFragment = new RaceFragment();
            }
            this.currentFragment = raceFragment;
        } else if (this.currentFragmentTag.equals(HELP)) {
            if (this.helpFragment == null) {
                this.helpFragment = new HelpFragment();
            }
            this.currentFragment = helpFragment;
        }
        // pop backstack if there is anything to pop;
        // in case user chooses fragments from drawer without
        // pressing 'back'
        boolean popped = this.fragmentManager.popBackStackImmediate();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "showFragment(); popped=" + popped);
        }
        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentLayout, currentFragment, currentFragmentTag);
        if (ENGSPA.equals(previousFragmentTag)) {
            transaction.addToBackStack(ENGSPA);
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
				"onUpdateUser(" + userName + ", " + userLevel +
				", " + qaStyle + ")");
		if (userName.length() < 1) {
			this.statusTextView.setText("no user name supplied");
			return;
		}
		if (userLevel < 1) {
			this.statusTextView.setText("invalid userLevel supplied");
			return;
		}
		boolean isNewLevel = this.viewlessFragment.setUser(userName, userLevel, qaStyle);
        if (isNewLevel) {
            if (this.engSpaFragment != null) {
                // if null, nothing to do!
                this.engSpaFragment.newUserLevel();
            }
        }
	}
	@Override // UserSettingsListener && EngSpaActivity
	public EngSpaUser getEngSpaUser() {
        return this.viewlessFragment.getEngSpaUser();
	}

    @Override // EngSpaActivity
	public void setStatus(int statusId) {
		this.statusTextView.setText(statusId);
	}

	@Override // EngSpaActivity
	public void setStatus(String statusText) {
		this.statusTextView.setText(statusText);
	}

	@Override // EngSpaActivity
	public void onLost() {
        this.viewlessFragment.onLost();
	}

	@Override // EngSpaActivity
	public void onWrongAnswer() {
        this.viewlessFragment.onWrongAnswer();
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
		if (questionSeq == 0 && BuildConfig.DEBUG) {
			Log.w(TAG, "getQuestionSequence() returning zero");
		}
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(questionSequenceKey, ++questionSeq);
		editor.commit();
		return questionSeq;
	}
	@Override // EngSpaActivity
	public void setSpanish(String spanish) {
        this.viewlessFragment.setSpanish(spanish);
	}
	@Override // EngSpaActivity
	public void speakSpanish(String spanish) {
        this.viewlessFragment.speakSpanish(spanish);
	}

	@Override // EngSpaActivity
	public void setAppBarTitle(int resId) {
		super.setTitle(resId);
	}

	@Override // EngSpaActivity
	public void setAppBarTitle(String title) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"setEngSpaTitle(" + title + ")");
		super.setTitle(title);
	}
	@Override // EngSpaActivity
	public void setProgressBarVisible(boolean visible) {
		progressBar.setVisibility(visible ? ProgressBar.VISIBLE : ProgressBar.GONE);
	}
	@Override // EngSpaActivity
	public SharedPreferences getSharedPreferences() {
		return this.sharedPreferences;
	}
	@Override // EngSpaActivity
	public EngSpaDAO getEngSpaDAO() {
		if (this.engSpaDAO == null) {
			this.engSpaDAO = EngSpaSQLite2.getInstance(getApplicationContext());
		}
		return this.engSpaDAO;
	}
}
