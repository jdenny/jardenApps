package com.jardenconsulting.spanishapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jarden.app.race.RaceFragment;
import jarden.document.DocumentTextView;
import jarden.engspa.EngSpa;
import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaQuiz;
import jarden.engspa.EngSpaSQLite2;
import jarden.engspa.EngSpaUser;
import jarden.engspa.EngSpaUtils;
import jarden.http.MyHttpClient;
import jarden.provider.engspa.EngSpaContract.QAStyle;
import jarden.quiz.QuizCache;

import com.jardenconsulting.spanishapp.UserDialog.UserSettingsListener;

import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
		implements EngSpaActivity, UserSettingsListener,
		TopicDialog.TopicListener, QAStyleDialog.QAStyleListener,
		NavigationView.OnNavigationItemSelectedListener,
        ListView.OnItemLongClickListener, View.OnClickListener {

	//!! private static final String TAG = "MainActivity";
	private static final int[] helpResIds = {
            R.string.engSpaHelp,
            R.string.EngSpaMoreHelp,
            R.string.FeedbackHelp,
            R.string.MenuHelp,
            R.string.micButtonHelp,
            R.string.MicButtonMoreHelp,
            R.string.NumbersGameHelp,
            R.string.NumbersGameMoreHelp,
            R.string.QuestionsByLevelHelp,
            R.string.QuestionStyleHelp,
            R.string.SelectTopicHelp,
            R.string.SelfMarkHelp,
            R.string.SelfMarkMoreHelp,
            R.string.WordLookupHelp
    };
	private static final String ENGSPA_TXT_VERSION_KEY = "EngSpaTxtVersion";
    private String SHOW_HELP_KEY = "SHOW_HELP_KEY";
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
    private static final String VIEWLESS = "VIEWLESS";
    // end of Fragment tags
    private static String questionSequenceKey = null;
	private EngSpaDAO engSpaDAO;
	private FragmentManager fragmentManager;
	private EngSpaFragment engSpaFragment;
	private WordLookupFragment wordLookupFragment;
	private RaceFragment raceFragment;
	private Fragment currentFragment;
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
    private TextView helpTextView;
    private DocumentTextView documentTextView;
	private boolean doubleBackToExitPressedOnce = false;
    private CheckBox showHelpCheckBox;
    private AudioModeDialog audioModeDialog;
    private AlertDialog alertDialog;

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

        this.helpTextView = (TextView) findViewById(R.id.helpTextView);
        this.statusTextView = (TextView) findViewById(R.id.statusTextView);

        this.showHelpCheckBox = (CheckBox) findViewById(R.id.showHelpCheckBox);
        this.showHelpCheckBox.setOnClickListener(this);
        boolean isShowHelp = sharedPreferences.getBoolean(SHOW_HELP_KEY, true);
        if (!isShowHelp) {
            this.helpTextView.setVisibility(View.GONE);
        }
        this.showHelpCheckBox.setChecked(isShowHelp);

        this.documentTextView = new DocumentTextView(
                getApplicationContext(),
                helpTextView, helpResIds, null);
        helpTextView.setMovementMethod(LinkMovementMethod.getInstance());
        //?? helpTextView.setMovementMethod(new ScrollingMovementMethod());

        helpTextView.setHighlightColor(Color.TRANSPARENT);

		this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
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
            this.currentFragmentTag = ENGSPA;
            setTip(R.string.FeedbackHelp);
            loadDB(); // which in turn -> dbLoadComplete() -> showFragment()
		} else {
			this.currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            this.viewlessFragment =
                    (ViewlessFragment) fragmentManager.findFragmentByTag(VIEWLESS);
			this.wordLookupFragment =
                    (WordLookupFragment) fragmentManager.findFragmentByTag(WORD_LOOKUP);
			this.raceFragment = (RaceFragment) fragmentManager.findFragmentByTag(NUMBER_GAME);
            this.engSpaFragment = (EngSpaFragment) fragmentManager.findFragmentByTag(ENGSPA);
            if (this.currentFragmentTag == null) this.currentFragmentTag = ENGSPA;
            showFragment();
		}
	}
    private void dbLoadComplete() {
        showFragment();
        checkForDBUpdates();
    }

    @Override // OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.showHelpCheckBox) {
            setShowHelp(this.showHelpCheckBox.isChecked());
        }
    }
    @Override // Activity
    public void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) Log.d(TAG, "onResume()");
    }
	@Override // EngSpaActivity
    public void setTip(int resId) {
        if (!this.documentTextView.showPage(getResources().getResourceEntryName(resId))) {
            // so that it doesn't all lock up if resId not found:
            this.helpTextView.setText((resId));
        }
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
                    // Note: this code is sometimes run before end of onCreate()
                    //!! final SharedPreferences sharedPreferences = getSharedPreferences();
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
        if (BuildConfig.DEBUG) {
            // programmatically add debug items:
            MenuItem findDupsItem = menu.add("Find Duplicates");
            findDupsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    findDuplicates();
                    return true;
                }
            });
        }
		return true;
	}
    @Override // OnNavigationItemSelectedListener
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected(menuItem.id=" + id + ")");
        if (id == R.id.qaStyle) {
            if (this.qaStyleDialog == null) this.qaStyleDialog = new QAStyleDialog();
            this.qaStyleDialog.show(getSupportFragmentManager(), "QAStyleDialog");
        } else if (id == R.id.topicMode) {
            showTopicDialog();
        } else if (id == R.id.wordLookup) {
            showFragment(WORD_LOOKUP);
        } else if (id == R.id.numbersGame) {
            showFragment(NUMBER_GAME);
        } else if (id == R.id.learnMode) {
            showEngSpaFragment(EngSpaQuiz.QuizMode.LEARN);
        } else if (id == R.id.exit) {
            super.onBackPressed();
        } else if (id == R.id.audioMode) {
            int level = getEngSpaUser().getUserLevel();
            if (level < 2) {
                showAlertDialog(R.string.audioMode);
            } else {
                if (this.audioModeDialog == null) {
                    this.audioModeDialog = new AudioModeDialog();
                }
                this.engSpaFragment.setQuizMode(EngSpaQuiz.QuizMode.AUDIO);
                this.audioModeDialog.show(getSupportFragmentManager(), "AudioModeDialog");
            }
        } else if (id == R.id.practiceMode) {
            int level = getEngSpaUser().getUserLevel();
            if (level < 2) {
                showAlertDialog(R.string.practiceMode);
            } else {
                showEngSpaFragment(EngSpaQuiz.QuizMode.PRACTICE);
            }
        } else {
            Log.e(TAG, "unrecognised drawer menu item id: " + id);
        }
        this.drawerLayout.closeDrawers();
        return true;
    }
    private void showAlertDialog(int titleRes) {
        if (this.alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.userLevelError)
                    .setPositiveButton("OK", null);
            this.alertDialog = builder.create();
        }
        this.alertDialog.setTitle(titleRes);
        this.alertDialog.show();

    }
    private void setShowHelp(boolean showHelp) {
        this.helpTextView.setVisibility(showHelp ? View.VISIBLE : View.GONE);
        SharedPreferences.Editor editor =
                this.sharedPreferences.edit();
        editor.putBoolean(SHOW_HELP_KEY, showHelp);
        editor.apply();
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
				"onOptionsItemSelected(itemId=" + id + ")");
        if (id == R.id.setUserLevel) {
			if (this.userDialog == null) this.userDialog = new UserDialog();
			this.userDialog.show(getSupportFragmentManager(), "UserSettingsDialog");
		} else if (id == R.id.speakerButton) {
            boolean spoken = this.viewlessFragment.speakSpanish();
            if (!spoken) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "speakButton(); this.spanish==null");
                }
                this.setStatus(R.string.spanishNull);
            }
		} else if (id == R.id.deleteAllFails) {
            this.viewlessFragment.getEngSpaQuiz().deleteAllFails();
		} else {
            return super.onOptionsItemSelected(item);
        }
        return true;
	}
    private void findDuplicates() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (engSpaDAO == null) {
                    engSpaDAO = EngSpaSQLite2.getInstance(getApplicationContext());
                }
                // for each word, using word id
                // find words by English
                // find words by Spanish
                int dbSize = engSpaDAO.getDictionarySize();
                if (dbSize == 0) dbSize = 100;
                Log.i(TAG, "findDuplicates(); dbSize=" + dbSize);
                EngSpa engSpa;
                String english, spanish;
                List<EngSpa> englishList, spanishList;
                for (int id = 1; id < dbSize; id++) {
                    engSpa = engSpaDAO.getWordById(id);
                    english = engSpa.getEnglish();
                    englishList = engSpaDAO.getEnglishWord(english);
                    if (englishList.size() > 1) {
                        Log.i(TAG, "duplicates for " + english);
                        for (EngSpa es: englishList) {
                            Log.i(TAG, "  " + es);
                        }
                    }
                    spanish = engSpa.getSpanish();
                    spanishList = engSpaDAO.getSpanishWord(spanish);
                    if (spanishList.size() > 1) {
                        Log.i(TAG, "duplicates for " + spanish);
                        for (EngSpa es: spanishList) {
                            Log.i(TAG, "  " + es);
                        }
                    }
                }
                Log.i(TAG, "end of findDuplicates()");
            }
        }).start();
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
		showEngSpaFragment(EngSpaQuiz.QuizMode.TOPIC);
        this.engSpaFragment.setTopic(topic);

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
    private void showEngSpaFragment(EngSpaQuiz.QuizMode quizMode) {
        if (this.engSpaFragment == null) {
            this.engSpaFragment = new EngSpaFragment();
        }
        this.engSpaFragment.setQuizMode(quizMode);
        showFragment(ENGSPA);
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
						editor.apply();
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
	 * Called on completion of UserDialog.
	 */
	@Override // UserSettingsListener
    public void onUpdateUserLevel(int userLevel) {
        if (BuildConfig.DEBUG) Log.d(TAG,
                "onUpdateUserLevel(" + userLevel + ")");
        if (userLevel < 1) {
            this.statusTextView.setText(R.string.invalidUserLevel);
            return;
        }
        boolean isNewLevel = this.viewlessFragment.setUserLevel(userLevel);
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
		editor.apply();
		return questionSeq;
	}
	@Override // EngSpaActivity
	public void setSpanish(String spanish) {
        this.viewlessFragment.setSpanish(spanish);
	}
	@Override // EngSpaActivity
	public void speakEnglish(String english) {
        this.viewlessFragment.speakEnglish(english);
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
	/*!!
	@Override // EngSpaActivity
	public SharedPreferences getSharedPreferences() {
		return this.sharedPreferences;
	}
	*/
	@Override // EngSpaActivity
	public EngSpaDAO getEngSpaDAO() {
		if (this.engSpaDAO == null) {
			this.engSpaDAO = EngSpaSQLite2.getInstance(getApplicationContext());
		}
		return this.engSpaDAO;
	}

    @Override // EngSpaActivity
    public EngSpaQuiz getEngSpaQuiz() {
        return viewlessFragment.getEngSpaQuiz();
    }

}
