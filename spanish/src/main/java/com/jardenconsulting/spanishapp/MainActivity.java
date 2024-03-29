package com.jardenconsulting.spanishapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.jardenconsulting.spanishapp.UserDialog.UserSettingsListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

public class MainActivity extends AppCompatActivity
		implements EngSpaActivity, UserSettingsListener,
		TopicDialog.TopicListener, QAStyleDialog.QAStyleListener,
		NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private static final String TAG = "Spanish";
	private static final int[] helpResIds = {
            R.string.Contents, // first in list shown initially
            R.string.Introduction,
            R.string.Audio_Mode,
            R.string.Feedback,
			R.string.Learn_Mode,
            R.string.Main_Menu,
			R.string.Mic_Button,
			R.string.Numbers_Game,
            R.string.Practice_Mode,
			R.string.Question_Style,
			R.string.Self_Mark,
            R.string.Topic_Mode,
			R.string.Word_Lookup,
            R.string.clearAnswerTip,
            R.string.correctButtonTip,
            R.string.statsTip,
            R.string.goButtonTip,
            R.string.incorrectButtonTip,
            R.string.resetButtonTip,
            R.string.speakerButtonTip,
            R.string.tryGoAgainTip
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
	private boolean doubleBackToExitPressedOnce = false;
	private CheckBox showHelpCheckBox;
	private AudioModeDialog audioModeDialog;
	private AlertDialog alertDialog;

	@Override // Activity
	protected void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
				(savedInstanceState==null?"":"not ") + "null)");
		super.onCreate(savedInstanceState);
		getEngSpaDAO();
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		this.helpTextView = findViewById(R.id.helpTextView);
		this.statusTextView = findViewById(R.id.statusTextView);
        this.statusTextView.setVisibility(View.GONE); // hidden if no message shown
		this.showHelpCheckBox = findViewById(R.id.showHelpCheckBox);
		this.showHelpCheckBox.setOnClickListener(this);
		helpTextView.setMovementMethod(LinkMovementMethod.getInstance());
		//?? helpTextView.setMovementMethod(new ScrollingMovementMethod());
		helpTextView.setHighlightColor(Color.TRANSPARENT);
		this.progressBar = findViewById(R.id.progressBar);
		this.drawerLayout = findViewById(R.id.drawer_layout);
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
		NavigationView navigationView = findViewById(R.id.nav_view);
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
            getDocumentTextView().setTextView(this.helpTextView);
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
    private DocumentTextView getDocumentTextView() {
        DocumentTextView documentTextView = viewlessFragment.getDocumentTextView();
        if (documentTextView == null) {
            documentTextView = new DocumentTextView(
                    getApplicationContext(),
                    helpTextView, helpResIds, null);
            viewlessFragment.setDocumentTextView(documentTextView);
        }
        return documentTextView;
    }
    private void hideHelp() {
        this.helpTextView.setVisibility(View.GONE);
    }
    private void dbLoadComplete() {
		showFragment();
		/* checkForDBUpdates() allows us to update the database by uploading
		   a text file to Google sites, rather than reissuing the complete app;
		   we have found that, in practice, this is more trouble than it's worth,
		   so we are dropping the idea, but keeping the code in place for possible
		   future use.
		 */
		// checkForDBUpdates();
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
        getDocumentTextView().showPage(resId);
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
            if (engSpaFragment != null) engSpaFragment.reset();
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
			if (level < 3) {
				showAlertDialog(R.string.userLevelErrorPractice);
			} else {
				engSpaQuiz.setQuizMode(QuizMode.PRACTICE);
                if (engSpaFragment != null) engSpaFragment.reset();
				showFragment(FragmentTag.ENGSPA);
			}
		} else {
			Log.e(TAG, "unrecognised drawer menu item id: " + id);
		}
		this.drawerLayout.closeDrawers();
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
    /**
     * Set help views visible if not already so.
     */
    @Override
    public void showHelp() {
        if (this.helpTextView.getVisibility() != View.VISIBLE) {
            this.showHelpCheckBox.setChecked(true);
            setShowHelp(true);
        }
    }
	private void setShowHelp(boolean showHelp) {
        if (showHelp) {
            this.helpTextView.setVisibility(View.VISIBLE);
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
        } else if (id == R.id.listAllFails) {
            List<EngSpa> allFails = this.viewlessFragment.getEngSpaQuiz().getAllFails();
            StringBuilder strBuilder = new StringBuilder();
            for (EngSpa fail: allFails) {
                strBuilder.append(fail.getSpanish() + "," + fail.getEnglish() + "\n");
                // strBuilder.append(fail.getDictionaryString()).append('\n');
            }
            this.helpTextView.setText(strBuilder.toString());
            showHelp();
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
            showFragment(FragmentTag.ENGSPA);
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
        if (engSpaFragment != null) this.engSpaFragment.reset();
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
			if (BuildConfig.DEBUG) {
                Log.d(TAG,
                        "showFragment(" + fragmentTag +
                        "); already current fragment");
            }
            if (fragmentTag == FragmentTag.ENGSPA) {
                // could be different QuizMode (hence different title) of EngSpaFragment
                setAppBarTitle();
            }
			return;
		}
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
		FragmentTransaction transaction = this.fragmentManager.beginTransaction();
		transaction.replace(R.id.fragmentLayout, currentFragment, currentFragmentTag.name());
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
	@Override // IntValueListener
	public void onUpdateUserLevel(int userLevel) {
		if (BuildConfig.DEBUG) Log.d(TAG,
				"onUpdateIntValue(" + userLevel + ")");
		if (userLevel < 1 || userLevel > this.engSpaDAO.getMaxUserLevel()) {
			this.statusTextView.setText(R.string.invalidUserLevel);
			return;
		}
		QuizMode quizMode = getEngSpaUser().getQuizMode();
		if (userLevel == 1 && quizMode == QuizMode.PRACTICE) {
			this.statusTextView.setText(R.string.invalidUserLevelForMode);
			return;
		}
		if (userLevel == getEngSpaUser().getLearnLevel()) {
			this.statusTextView.setText(R.string.userNotChanged);
			return;
		}
		getEngSpaQuiz().setUserLevel(userLevel);
		if (this.currentFragmentTag == FragmentTag.ENGSPA &&
                this.engSpaFragment != null) this.engSpaFragment.reset();
	}
	@Override // IntValueListener && EngSpaActivity
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
