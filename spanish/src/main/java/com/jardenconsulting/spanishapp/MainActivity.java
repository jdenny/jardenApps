package com.jardenconsulting.spanishapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.List;

import jarden.app.race.RaceFragment;
import jarden.document.DocumentTextView;
import jarden.engspa.EngSpa;
import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaQuiz;
import jarden.engspa.EngSpaQuiz.QuizMode;
import jarden.engspa.EngSpaSQLite2;
import jarden.engspa.EngSpaUser;
import jarden.engspa.EngSpaUtils;
import jarden.http.MyHttpClient;
import jarden.provider.engspa.EngSpaContract.QAStyle;
import jarden.quiz.QuizCache;

import com.jardenconsulting.spanishapp.UserDialog.UserSettingsListener;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
		implements EngSpaActivity, UserSettingsListener,
		TopicDialog.TopicListener, QAStyleDialog.QAStyleListener,
		NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";
	private static final int[] helpResIds = {
            R.string.IntroHelp, // 1st in list shown initially
            R.string.ContentsHelp,
            R.string.AudioModeHelp,
            R.string.FeedbackHelp,
			R.string.LearnModeHelp,
            R.string.MenuHelp,
			R.string.MicButtonHelp,
			R.string.NumbersGameHelp,
            R.string.PracticeModeHelp,
			R.string.QuestionStyleHelp,
			R.string.SelfMarkHelp,
            R.string.TopicModeHelp,
			R.string.WordLookupHelp
	};
	private static final String ENGSPA_TXT_VERSION_KEY = "EngSpaTxtVersion";
    private static final int TOPIC_FOR_TITLE = -1;
	private static final String UPDATES_VERSION_KEY = "DataVersion";
	private static final String ENG_SPA_UPDATES_NAME =
			QuizCache.serverUrlStr + "engspaupdates.txt?attredirects=0&d=1";
    private static final String STATUS_TEXT_KEY = "StatusText";

    // Fragment tags:
	private enum FragmentTag {
		WORD_LOOKUP, NUMBER_GAME, ENGSPA, VIEWLESS
	}
	private static final String CURRENT_FRAGMENT_KEY =
			"currentFragmentTag";
	private FragmentTag previousFragmentTag;
    private FragmentTag currentFragmentTag;
	private static String questionSequenceKey = null;
	private EngSpaDAO engSpaDAO;
	private FragmentManager fragmentManager;
	private EngSpaFragment engSpaFragment;
	private WordLookupFragment wordLookupFragment;
	private RaceFragment raceFragment;
	private Fragment currentFragment;
	private ViewlessFragment viewlessFragment;
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
    private Button helpContentsButton;
    private Button helpBackButton;
	private AudioModeDialog audioModeDialog;
	private AlertDialog alertDialog;

	@Override // Activity
	protected void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
				(savedInstanceState==null?"":"not ") + "null)");
		super.onCreate(savedInstanceState);
		getEngSpaDAO();
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		this.helpTextView = (TextView) findViewById(R.id.helpTextView);
		this.statusTextView = (TextView) findViewById(R.id.statusTextView);
        this.statusTextView.setVisibility(View.GONE); // hidden if no message is shown
		this.showHelpCheckBox = (CheckBox) findViewById(R.id.showHelpCheckBox);
		this.showHelpCheckBox.setOnClickListener(this);
        this.helpContentsButton = (Button) findViewById(R.id.helpContentsButton);
        helpContentsButton.setOnClickListener(this);
        this.helpBackButton = (Button) findViewById(R.id.helpBackButton);
        helpBackButton.setOnClickListener(this);
        helpBackButton.setVisibility(View.GONE); // until further notice!!
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
				R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                closeKeyboard();
            }
        };
		drawerLayout.addDrawerListener(drawerToggle);
		drawerToggle.syncState();
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		this.fragmentManager = getSupportFragmentManager();
		if (savedInstanceState == null) {
			this.viewlessFragment = new ViewlessFragment();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(this.viewlessFragment, FragmentTag.VIEWLESS.name());
			ft.commit();
			this.currentFragmentTag = FragmentTag.ENGSPA;
			loadDB(); // which in turn -> dbLoadComplete() -> showFragment()
		} else {
            setStatus(savedInstanceState.getString(STATUS_TEXT_KEY));
            String currentFragmentStr = savedInstanceState.getString(CURRENT_FRAGMENT_KEY);
            this.currentFragmentTag = (currentFragmentStr == null) ?
                FragmentTag.ENGSPA : FragmentTag.valueOf(currentFragmentStr);
			this.viewlessFragment = (ViewlessFragment) fragmentManager.findFragmentByTag(
					FragmentTag.VIEWLESS.name());
			this.wordLookupFragment = (WordLookupFragment) fragmentManager.findFragmentByTag(
                    FragmentTag.WORD_LOOKUP.name());
			this.raceFragment = (RaceFragment) fragmentManager.findFragmentByTag(
                    FragmentTag.NUMBER_GAME.name());
			this.engSpaFragment = (EngSpaFragment) fragmentManager.findFragmentByTag(
                    FragmentTag.ENGSPA.name());
			showFragment();
		}
	}
    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) Log.d(TAG, "onStart()");
        boolean isShowHelp = viewlessFragment.getEngSpaUser().isShowHelp();
        if (!isShowHelp) {
            hideHelp();
        }
        this.showHelpCheckBox.setChecked(isShowHelp);
    }
    private void hideHelp() {
        this.helpTextView.setVisibility(View.GONE);
        this.helpContentsButton.setVisibility(View.GONE);
        this.helpBackButton.setVisibility(View.GONE);
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
		} else if (id == R.id.helpContentsButton) {
            setTip(R.string.ContentsHelp);
        } else if (id == R.id.helpBackButton) {
            Deque<Integer> helpHistory = this.viewlessFragment.getHelpHistory();
            if (helpHistory.isEmpty()) {
                Toast.makeText(this, "help history empty", Toast.LENGTH_LONG).show();
            } else {
                int helpId = helpHistory.pop();
                setTip(helpId);
            }
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
            Deque<Integer> helpHistory = this.viewlessFragment.getHelpHistory();
            helpHistory.push(resId);
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
					final int savedVersion = getSharedPreferences().getInt(ENGSPA_TXT_VERSION_KEY, 0);
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
		EngSpaQuiz engSpaQuiz = getEngSpaQuiz();
		if (id == R.id.qaStyle) {
            if (getEngSpaUser().getQuizMode() == QuizMode.LEARN) {
                showAlertDialog(R.string.questionStyleError);
            } else {
                if (this.qaStyleDialog == null) this.qaStyleDialog = new QAStyleDialog();
                this.qaStyleDialog.show(getSupportFragmentManager(), "QAStyleDialog");
            }
		} else if (id == R.id.topicMode) {
			showTopicDialog();
		} else if (id == R.id.wordLookup) {
			showFragment(FragmentTag.WORD_LOOKUP);
		} else if (id == R.id.numbersGame) {
			showFragment(FragmentTag.NUMBER_GAME);
		} else if (id == R.id.learnMode) {
			engSpaQuiz.setQuizMode(QuizMode.LEARN);
            engSpaFragment.reset();
			showFragment(FragmentTag.ENGSPA);
		} else if (id == R.id.exit) {
			super.onBackPressed();
		} else if (id == R.id.audioMode) {
			int level = getEngSpaUser().getLearnLevel();
			if (level < 2) {
				showAlertDialog(R.string.userLevelErrorAudio);
			} else {
                showAudioModeDialog();
			}
		} else if (id == R.id.practiceMode) {
			int level = getEngSpaUser().getLearnLevel();
			if (level < 2) {
				showAlertDialog(R.string.userLevelErrorPractice);
			} else {
				engSpaQuiz.setQuizMode(QuizMode.PRACTICE);
                engSpaFragment.reset();
				showFragment(FragmentTag.ENGSPA);
			}
		} else {
			Log.e(TAG, "unrecognised drawer menu item id: " + id);
		}
		this.drawerLayout.closeDrawers();
        //!! setAppBarTitle(); // what does this do??
		return true;
	}
    private void showAlertDialog(int messageId) {
        showAlertDialog(getString(messageId));
    }
	private void showAlertDialog(String message) {
		if (this.alertDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton("OK", null);
			this.alertDialog = builder.create();
		}
		this.alertDialog.setMessage(message);
		this.alertDialog.show();

	}
    @Override
    /**
     * Set help views visible if not already so.
     */
    public void setShowHelp() {
        if (this.helpTextView.getVisibility() != View.VISIBLE) {
            setShowHelp(true);
        }
    }
	private void setShowHelp(boolean showHelp) {
        if (showHelp) {
            this.helpTextView.setVisibility(View.VISIBLE);
            this.helpContentsButton.setVisibility(View.VISIBLE);
            this.helpBackButton.setVisibility(View.GONE); // until further notice!!
        } else hideHelp();
        this.viewlessFragment.getEngSpaUser().setShowHelp(showHelp);
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
		if (FragmentTag.ENGSPA == this.currentFragmentTag) {
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
			this.currentFragmentTag = FragmentTag.ENGSPA;
			this.currentFragment = this.engSpaFragment;
            setAppBarTitle();
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
		getEngSpaQuiz().setQuizMode(QuizMode.TOPIC);
        this.getEngSpaUser().setTopic(topic);
        this.engSpaFragment.reset();
		showFragment(FragmentTag.ENGSPA);
	}
	@Override // QAStyleDialog.QAStyleListener
	public void onQAStyleSelected(QAStyle qaStyle) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"onQAStyleSelected(" + qaStyle + ")");
		getEngSpaUser().setQAStyle(qaStyle);
		showFragment(FragmentTag.ENGSPA);
	}
	@Override // Activity
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onSaveInstanceState(); currentFragmentTag=" +
					this.currentFragmentTag);
		}
		if (this.currentFragment != null) {
			savedInstanceState.putString(CURRENT_FRAGMENT_KEY,
					this.currentFragmentTag.name());
		}
        savedInstanceState.putString(STATUS_TEXT_KEY,
                this.statusTextView.getText().toString());
		super.onSaveInstanceState(savedInstanceState);
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
				long savedVersion = getSharedPreferences().getLong(UPDATES_VERSION_KEY, 0);
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

	private void showFragment(FragmentTag fragmentTag) {
		if (this.currentFragmentTag != null && fragmentTag == this.currentFragmentTag) {
			if (BuildConfig.DEBUG) Log.d(TAG,
					"showFragment(" + fragmentTag +
					"); already current fragment");
            if (fragmentTag == FragmentTag.ENGSPA) {
                // could be different QuizMode (hence different title) of EngSpaFragment
                setAppBarTitle();
            }
			return;
		}
		this.previousFragmentTag = this.currentFragmentTag;
		this.currentFragmentTag = fragmentTag;
		showFragment();
	}
	private void showFragment() {
		if (this.currentFragmentTag == FragmentTag.ENGSPA) {
			if (this.engSpaFragment == null) {
				this.engSpaFragment = new EngSpaFragment();
            }
			this.currentFragment = engSpaFragment;
		} else if (this.currentFragmentTag == FragmentTag.WORD_LOOKUP) {
			if (this.wordLookupFragment == null) {
				this.wordLookupFragment = new WordLookupFragment();
			}
			this.currentFragment = wordLookupFragment;
		} else if (this.currentFragmentTag == FragmentTag.NUMBER_GAME) {
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
		transaction.replace(R.id.fragmentLayout, currentFragment, currentFragmentTag.name());
		if (FragmentTag.ENGSPA == previousFragmentTag) {
			transaction.addToBackStack(FragmentTag.ENGSPA.name());
		}
		transaction.commit();
        setAppBarTitle();
	}
    private void showAudioModeDialog() {
        if (this.audioModeDialog == null) {
            this.audioModeDialog = new AudioModeDialog();
        }
        this.audioModeDialog.show(getSupportFragmentManager(), "AudioModeDialog");
    }
    private void closeKeyboard() {
        if (BuildConfig.DEBUG) Log.d(TAG, "closeKeyboard()");
        View currentView = this.getCurrentFocus();
        if (currentView != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
    }

	/**
	 * Called on completion of UserDialog.
	 */
	@Override // UserSettingsListener
	public void onUpdateUserLevel(int userLevel) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"onUpdateUserLevel(" + userLevel + ")");
		if (userLevel < 1 || userLevel > this.engSpaDAO.getMaxUserLevel()) {
			this.statusTextView.setText(R.string.invalidUserLevel);
			return;
		}
		QuizMode quizMode = getEngSpaUser().getQuizMode();
        //!! if (userLevel == 1 && quizMode == (QuizMode.PRACTICE || quizMode == QuizMode.AUDIO)) {
		if (userLevel == 1 && quizMode == QuizMode.PRACTICE) {
			this.statusTextView.setText(R.string.invalidUserLevelForMode);
			return;
		}
		if (userLevel == getEngSpaUser().getLearnLevel()) {
			this.statusTextView.setText(R.string.userNotChanged);
			return;
		}
		getEngSpaQuiz().setUserLevel(userLevel);
		if (this.currentFragmentTag == FragmentTag.ENGSPA) this.engSpaFragment.reset();
	}
	@Override // UserSettingsListener && EngSpaActivity
	public EngSpaUser getEngSpaUser() {
		return this.viewlessFragment.getEngSpaUser();
	}

	@Override // EngSpaActivity
	public void setStatus(int statusId) {
        if (statusId == CLEAR_STATUS) clearStatus();
        else {
            this.statusTextView.setText(statusId);
            this.statusTextView.setVisibility(View.VISIBLE);
        }
	}
    private void clearStatus() {
        this.statusTextView.setText("");
        this.statusTextView.setVisibility(View.GONE);
    }
	@Override // EngSpaActivity
	public void setStatus(String statusText) {
        if (statusText == null || statusText.length() == 0) clearStatus();
        else {
            this.statusTextView.setText(statusText);
            this.statusTextView.setVisibility(View.VISIBLE);
        }
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
			questionSequenceKey = "QSN_1";
		}
		int questionSeq = getSharedPreferences().getInt(questionSequenceKey, 0);
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
    public void setAppBarTitle() {
        EngSpaUser engSpaUser =  getEngSpaUser();
        QuizMode quizMode = engSpaUser.getQuizMode();
        int titleId = R.string.app_name;
        if (currentFragmentTag == FragmentTag.WORD_LOOKUP) {
            titleId = R.string.wordLookup;
        } else if (currentFragmentTag == FragmentTag.NUMBER_GAME) {
            titleId = R.string.numbersGame;
        } else { // must be ENGSPA
            if (quizMode == QuizMode.PRACTICE) {
                titleId = R.string.practiceMode;
            } else if (quizMode == QuizMode.TOPIC) {
                titleId = TOPIC_FOR_TITLE;
            } else if (quizMode == QuizMode.LEARN) {
                titleId = R.string.learnMode;
            }
        }
        setAppBarTitle(titleId);
    }
	private void setAppBarTitle(int titleId) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"setAppBarTitle(" + titleId + ")");
        if (titleId == TOPIC_FOR_TITLE) super.setTitle(getEngSpaUser().getTopic());
		else super.setTitle(titleId);
	}
	@Override // EngSpaActivity
	public void setProgressBarVisible(boolean visible) {
		progressBar.setVisibility(visible ? ProgressBar.VISIBLE : ProgressBar.GONE);
	}
	@Override // EngSpaActivity
	public SharedPreferences getSharedPreferences() {
		if (this.sharedPreferences == null) {
			this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
		}
		return this.sharedPreferences;
	}
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
