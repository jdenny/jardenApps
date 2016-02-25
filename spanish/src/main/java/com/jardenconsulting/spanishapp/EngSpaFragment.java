package com.jardenconsulting.spanishapp;

import jarden.provider.engspa.EngSpaContract.QAStyle;
import jarden.provider.engspa.EngSpaContract.VoiceText;
import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaQuiz;
import jarden.engspa.EngSpaQuiz.QuizEventListener;
import jarden.engspa.EngSpaUser;
import jarden.engspa.EngSpaUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EngSpaFragment extends Fragment implements OnClickListener,
		OnLongClickListener, OnEditorActionListener, QuizEventListener,
		OnInitListener {
	
	private static final int PHRASE_ACTIVITY_CODE = 1002;
	private static final Locale LOCALE_ES = new Locale("es", "ES");

	private TextView userNameTextView;
	private TextView currentCtTextView;
	private TextView failCtTextView;
	private TextView questionTextView;
	private TextView attributeTextView;
	private EditText answerEditText;
	private TextView statusTextView;
	private ViewGroup selfMarkLayout;
	private ViewGroup buttonLayout;
	private ImageButton micButton;
	private String levelStr;

	private Random random = new Random();
	private int orientation;
	private QAStyle currentQAStyle;
	private String question;
	private String spanish; // word to be spoken; used by all fragments
	/**
	 * Copy of this.spanish; used when user does 'back' to this
	 * fragment, in case other fragment has overwritten spanish
	 * with its own word.
	 */
	private String engSpaSpanish;
	private String correctAnswer;
	private String responseIfCorrect;
	private EngSpaQuiz engSpaQuiz;
	private EngSpaUser engSpaUser;
	private EngSpaDAO engSpaDAO;
	private EngSpaActivity engSpaActivity;
	private TextToSpeech textToSpeech;
	private int red;
	private int blue;
	/**
	 * Used by QAStyle.alternate, which is used to alternate
	 * between 2 QAStyles: spokenSpaToEng and writtenEngToSpa.
	 * If firstAlternative is true, then the style used for the
	 * next question is the spokenSpaToEng, else writtenEngToSpa.
	 */
	private boolean firstAlternative;

	@Override // Fragment
	public void onAttach(Activity activity) {
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "EngSpaFragment.onAttach()");
		super.onAttach(activity);
		this.engSpaActivity = (EngSpaActivity) activity;
	}
	@SuppressWarnings("deprecation")
	@Override // Fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) Log.d(engSpaActivity.getTag(), "EngSpaFragment.onCreate(" +
				(savedInstanceState==null?"":"not ") + "null)");
		setRetainInstance(true);
		Resources resources = getResources();
		this.red = resources.getColor(R.color.samRed);
		this.blue = resources.getColor(R.color.samBlue);
		this.levelStr = getResources().getString(R.string.levelStr);
		this.engSpaDAO = engSpaActivity.getEngSpaDAO();
		this.engSpaUser = engSpaDAO.getUser();
		if (this.engSpaUser == null) { // i.e. no user yet on database
			this.engSpaUser = new EngSpaUser("your name",
					1, QAStyle.writtenSpaToEng);
			engSpaDAO.insertUser(engSpaUser);
		}
		this.engSpaQuiz = new EngSpaQuiz(engSpaDAO, this.engSpaUser);
		this.engSpaQuiz.setQuizEventListener(this);
		if (savedInstanceState == null) {
			// i.e. clean run, not restart after Android
			// has destroyed app
			this.engSpaActivity.checkForDBUpdates();
		}
	}
	@Override // Fragment
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(engSpaActivity.getTag(),
					"EngSpaFragment.onCreateView(); question=" + question +
					"; savedInstanceState is " + (savedInstanceState==null?"":"not ") + "null");
		}
		// Potentially restore state after configuration change; before we re-create
		// the views, get relevant information from current values. See knowledgeBase.txt
		CharSequence questionText = null;
		CharSequence attributeText = null;
		CharSequence pendingAnswer = null;
		CharSequence statusText = null;
		int selfMarkLayoutVisibility = View.GONE;
		if (questionTextView != null) {
			questionText = questionTextView.getText();
			attributeText = attributeTextView.getText();
			pendingAnswer = answerEditText.getText();
			statusText = statusTextView.getText();
			selfMarkLayoutVisibility = selfMarkLayout.getVisibility();
		}
		// Now get new layout
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		this.userNameTextView = (TextView) rootView.findViewById(R.id.userNameTextView);
		this.userNameTextView.setText(this.engSpaUser.getUserName());
		this.currentCtTextView = (TextView) rootView.findViewById(R.id.currentCtTextView);
		this.failCtTextView = (TextView) rootView.findViewById(R.id.failCtTextView);
		failCtTextView.setOnLongClickListener(this);
		rootView.findViewById(R.id.failCtLabel).setOnLongClickListener(this);

		this.selfMarkLayout = (ViewGroup) rootView.findViewById(R.id.selfMarkLayout);
		this.buttonLayout = (ViewGroup) rootView.findViewById(R.id.buttonLayout);
		this.selfMarkLayout.setVisibility(View.GONE);
		Button button = (Button) rootView.findViewById(R.id.goButton);
		button.setOnClickListener(this);
		button.setOnLongClickListener(this);
		this.micButton = (ImageButton) rootView.findViewById(R.id.micButton);
		this.micButton.setOnClickListener(this);
		this.micButton.setOnLongClickListener(this);
		button = (Button) rootView.findViewById(R.id.incorrectButton);
		button.setOnClickListener(this);
		button.setOnLongClickListener(this);
		button = (Button) rootView.findViewById(R.id.correctButton);
		button.setOnClickListener(this);
		button.setOnLongClickListener(this);
		this.questionTextView = (TextView) rootView.findViewById(R.id.questionTextView);
		this.attributeTextView = (TextView) rootView.findViewById(R.id.attributeTextView);
		this.answerEditText = (EditText) rootView.findViewById(R.id.answerEditText);
		this.statusTextView = (TextView) rootView.findViewById(R.id.statusTextView);
		if (questionTextView != null) {
			this.questionTextView.setText(questionText);
			this.answerEditText.setText(pendingAnswer);
			this.attributeTextView.setText(attributeText);
			this.statusTextView.setText(statusText);
			if (selfMarkLayoutVisibility == View.VISIBLE) showSelfMarkLayout();
		}
		this.answerEditText.setOnEditorActionListener(this);
		if (this.spanish == null) {
			askQuestion(true);
			showUserLevel();
		} else {
			showStats();
		}
		saveOrientation();
		return rootView;
	}
	@Override // Fragment
	public void onResume() {
		if (BuildConfig.DEBUG) {
			Log.d(engSpaActivity.getTag(),
					"EngSpaFragment.onResume(); question=" + question +
					"; textToSpeech is " + (textToSpeech==null?"":"not ") + "null");
		}
		super.onResume();
		// askQuestion(false); // TODO: which to use?
		// if we askQuestion, does that mean we don't need to restore other
		// fields in onCreateView()?
		if (this.currentQAStyle.voiceText != VoiceText.text) {
			speakSpanish(this.spanish);
		}
	}
	/**
	 * if getNext is true: get next question
	 * askQuestion using UI (textFields & voice)
	 */
	private void askQuestion(boolean getNext) {
		if (getNext) {
			nextQuestion();
		}
		String hint = engSpaQuiz.getHint(!this.currentQAStyle.spaQuestion);
		if (hint.length() > 0) hint = "hint: " + hint;
		this.attributeTextView.setText(hint);
		if (this.currentQAStyle.voiceText != VoiceText.text) {
			speakSpanish(this.spanish);
		}
		if (this.currentQAStyle.spaAnswer) {
			this.answerEditText.setBackgroundColor(red);
			this.questionTextView.setBackgroundColor(blue);
			this.answerEditText.setHint(R.string.spanishStr);
			this.micButton.setVisibility(View.VISIBLE);
		} else {
			this.answerEditText.setBackgroundColor(blue);
			this.questionTextView.setBackgroundColor(red);
			this.answerEditText.setHint(R.string.englishStr);
			this.micButton.setVisibility(View.INVISIBLE);
		}
		if (this.currentQAStyle.voiceText == VoiceText.voice) {
			this.questionTextView.setText("");
		} else {
			this.questionTextView.setText(this.question);
		}
		showStats();
	}
	private boolean isUserLevelAll() {
		return this.engSpaUser.getUserLevel() == EngSpaQuiz.USER_LEVEL_ALL;
	}
	private void showStats() {
		int fwct = engSpaQuiz.getFailedWordCount();
		this.currentCtTextView.setText(
				isUserLevelAll() ? "" :
				Integer.toString(engSpaQuiz.getCurrentWordCount()));
		this.failCtTextView.setText(Integer.toString(fwct));
		if (BuildConfig.DEBUG) {
			String debugState = engSpaQuiz.getDebugState();
			Log.d(engSpaActivity.getTag(), debugState);
		}
	}
	private void nextQuestion() {
		this.spanish = engSpaQuiz.getNextQuestion2(
				engSpaActivity.getQuestionSequence());
		this.engSpaSpanish = this.spanish;
		String english = engSpaQuiz.getEnglish();
		
		// get qaStyle from question, if it was a failed word, or from user:
		this.currentQAStyle = engSpaQuiz.getQAStyleFromQuestion();
		if (this.currentQAStyle == null) { // i.e. if it's not a failed word
			this.currentQAStyle = this.engSpaUser.getQAStyle();
			if (this.currentQAStyle == QAStyle.random) {
				// minus 2 as we don't include random and alternate:
				int randInt = random.nextInt(QAStyle.values().length - 2);
				this.currentQAStyle = QAStyle.values()[randInt];
			} else if (this.currentQAStyle == QAStyle.alternate) {
				this.currentQAStyle = this.firstAlternative ? QAStyle.spokenSpaToEng
						: QAStyle.writtenEngToSpa;
				this.firstAlternative = !this.firstAlternative;
			}
		}
		this.responseIfCorrect = "Right!";
		if (this.currentQAStyle.spaQuestion) {
			this.question = spanish;
			if (this.currentQAStyle.spaAnswer) {
				this.responseIfCorrect = "Right! " + english;
			}
		} else {
			this.question = english;
		}
		if (this.currentQAStyle.spaAnswer) {
			this.correctAnswer = spanish;
		} else {
			this.correctAnswer = english;
		}
		this.answerEditText.getText().clear();
	}
	public EngSpaQuiz getEngSpaQuiz() {
		return this.engSpaQuiz;
	}

	@Override // onClickListener
	public void onClick(View view) {
		engSpaActivity.setStatus("");
		int id = view.getId();
		if (id == R.id.goButton) {
			goPressed();
		} else if (id == R.id.micButton) {
			startRecogniseSpeechActivity();
		} else if (id == R.id.correctButton) {
			selfMarkButton(true);
		} else if (id == R.id.incorrectButton) {
			selfMarkButton(false);
		}
	}
    @Override // OnLongClickListener
    public boolean onLongClick(View view) {
		int id = view.getId();
		if (id == R.id.goButton) {
			this.statusTextView.setText(R.string.goButtonTip);
			return true;
		} else if (id == R.id.correctButton) {
			this.statusTextView.setText(R.string.correctButtonTip);
			return true;
		} else if (id == R.id.incorrectButton) {
			this.statusTextView.setText(R.string.incorrectButtonTip);
			return true;
		} else if (id == R.id.micButton) {
			this.statusTextView.setText(R.string.micButtonTip);
			return true;
		} else if (id == R.id.failCtLabel || id == R.id.failCtTextView) {
			this.statusTextView.setText(R.string.failCtTip);
			return true;
		}
		return false;
    }
	@Override // OnInitListener (called when textToSpeech is initialised)
	public void onInit(int status) {
		if (BuildConfig.DEBUG) Log.d(engSpaActivity.getTag(), "EngSpaFragment.onInit()");
		engSpaActivity.setProgressBarVisible(false);
		this.statusTextView.setText("");
		if (status == TextToSpeech.SUCCESS) {
			if (this.textToSpeech == null) {
				// this could happen if activity is paused between creating
				// new textToSpeech and getting the response back here
				this.statusTextView.setText("textToSpeech closed down");
				return;
			}
			int result = textToSpeech.setLanguage(LOCALE_ES);
			if (BuildConfig.DEBUG) {
				Log.d(engSpaActivity.getTag(), "textToSpeech.setLanguage(); result=" + result);
			}
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				this.statusTextView.setText("TextToSpeech for Spanish is not supported");
			}
			if (this.spanish != null) speakSpanish2(); 
		} else {
			Log.w(engSpaActivity.getTag(), "EngSpaFragment.onInit(" + status + ")");
			this.statusTextView.setText(
				"Initilization of textToSpeech failed! Have you installed text-to-speech?");
		}
	}
	@Override // Fragment
	public void onPause() {
		boolean orientationChanged = isOrientationChanged();
		super.onPause();
		if (!orientationChanged &&
				this.textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
			textToSpeech = null;
			if (BuildConfig.DEBUG) Log.d(engSpaActivity.getTag(),
					"EngSpaFragment.onPause(); textToSpeech closed");
		}
	}
	// return true if orientation changed since previous call
	private boolean isOrientationChanged() {
		int oldOrientation = this.orientation;
		saveOrientation();
		if (BuildConfig.DEBUG) Log.d(engSpaActivity.getTag(),
				"EngSpaFragment.getOrientation(); orientation was: " +
				oldOrientation + ", is: " + this.orientation);
		return this.orientation != oldOrientation;
	}
	private void saveOrientation() {
		this.orientation = getResources().getConfiguration().orientation;
	}
	public void setSpanish(String spanish) {
		this.spanish = spanish;
	}
	public void speakSpanish(String spanish) {
		setSpanish(spanish);
		speakSpanish();
	}
	public void speakSpanish(boolean engSpaWord) {
		speakSpanish(engSpaWord?this.engSpaSpanish:this.spanish);
	}
	/**
	 * Ensure textToSpeech is initialised, then speak the
	 * current Spanish word, set by setSpanish(String spanish)
	 * or speakSpanish(String spanish).
	 */
	public void speakSpanish() {
		if (this.textToSpeech == null) {
			// invokes onInit() on completion
			textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), this);
			this.statusTextView.setText("loading textToSpeech...");
			engSpaActivity.setProgressBarVisible(true);
		} else {
			speakSpanish2();
		}
	}
	/**
	 * Part 2 of speakSpanish, invoked when textToSpeech initialised.
	 */
	@SuppressWarnings("deprecation")
	private void speakSpanish2() {
		textToSpeech.speak(this.spanish, TextToSpeech.QUEUE_ADD, null);
	}


	private void startRecogniseSpeechActivity() {
		Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
	    speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
		startActivityForResult(speechIntent, PHRASE_ACTIVITY_CODE);
	}
	/**
	 * Handle the results from the voice recognition activity.
	 * Runs on main (UI) thread.
	 */
	@Override // Fragment
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (BuildConfig.DEBUG) {
			Log.d(engSpaActivity.getTag(),
					"EngSpaFragment.onActivityResult(resultCode=" + resultCode);			
		}
		if (resultCode == Activity.RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			for (String match: matches) {
				Log.d(engSpaActivity.getTag(), "match=" + match);
				if (match.equalsIgnoreCase(this.correctAnswer)) {
					this.responseIfCorrect = "Right! " + match;
					setIsCorrect(true);
					return;
				}
			}
			// show no correct answer yet, but allow user another try
			// without penalty:
			this.statusTextView.setText(matches.get(0) + " is wrong");
		} else {
			this.statusTextView.setText(
					"resultCode from speech recognition: " + resultCode);
		}
	}
	private String getSuppliedAnswer() {
		return this.answerEditText.getText().toString().trim();
	}
	private void selfMarkButton(boolean isCorrect) {
		this.statusTextView.setText("");
		showButtonLayout();
		engSpaQuiz.setCorrect(isCorrect, currentQAStyle);
		askQuestion(true);
	}
	private void showSelfMarkLayout() {
		this.buttonLayout.setVisibility(View.GONE);
		this.selfMarkLayout.setVisibility(View.VISIBLE);
	}
	private void showButtonLayout() {
		this.selfMarkLayout.setVisibility(View.GONE);
		this.buttonLayout.setVisibility(View.VISIBLE);
	}
	/*
	if answer supplied:
		isCorrect = check if correct
		inform engSpaQuiz of result
		if correct: nextQuestion()
		showStats()
	else:
		switch button layout to selfMark (yes, no)
		display correctAnswer in answer field
		if spokenSpaToSpa also show English
	 */
	private void goPressed() {
		String suppliedAnswer = getSuppliedAnswer().trim();
		if (suppliedAnswer.length() == 0) {
			showSelfMarkLayout();
			this.answerEditText.setText(this.correctAnswer);
			if (this.currentQAStyle.voiceText == VoiceText.voice) {
				// if question was spoken only, user may want to see the translated word
				String translated = this.currentQAStyle.spaAnswer ? engSpaQuiz
						.getEnglish() : engSpaQuiz.getSpanish();
				this.questionTextView.setText(translated);
			}
		} else {
			String normalisedCorrectAnswer = normalise(this.correctAnswer);
			String normalisedSuppliedAnswer = normalise(suppliedAnswer);
			boolean isCorrect = normalisedCorrectAnswer.equals(normalisedSuppliedAnswer);
			if (!isCorrect && this.currentQAStyle.spaAnswer) {
				int res = EngSpaUtils.compareSpaWords(normalisedCorrectAnswer,
						normalisedSuppliedAnswer);
				if (res >= 0) {
					isCorrect = true;
					this.responseIfCorrect += " but note accent: " +
							this.correctAnswer + "; your answer: " +
							suppliedAnswer;
				}
			}
			setIsCorrect(isCorrect);
		}
	}
	private void setIsCorrect(boolean isCorrect) {
		setIsCorrect(isCorrect, "Wrong!");
	}
	private void setIsCorrect(boolean isCorrect, String responseIfWrong) {
		engSpaQuiz.setCorrect(isCorrect, currentQAStyle);
		if (isCorrect) {
			this.statusTextView.setText(this.responseIfCorrect);
		} else {
			this.statusTextView.setText(responseIfWrong);
		}
		askQuestion(isCorrect);
	}
	/*
	 * Normalise a word or phrase, to make the comparison more likely to succeed.
	 */
	@SuppressLint("DefaultLocale")
	private static String normalise(String text) {
		StringBuilder builder = new StringBuilder(text.toLowerCase());
		// 'they' in English can be 'ellos' or 'ellas' in Spanish; normalise 'ellas' to 'ellos'
		if (builder.length() >= 5 && builder.substring(0, 5).equals("ellas")) {
			builder.setCharAt(3, 'o');
		}
		// ! at end of imperatives is optional
		int builderLastCharIndex = builder.length() - 1;
		if (builder.charAt(builderLastCharIndex) == '!') {
			builder.deleteCharAt(builderLastCharIndex);
		}
		return builder.toString();
	}

	@Override // OnEditorActionListener
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_GO) {
			goPressed();
			handled = true;
		}
		return handled;
	}
	@Override // Fragment
	public void onDestroy() {
		if (BuildConfig.DEBUG) Log.d(engSpaActivity.getTag(), "EngSpaFragment.onDestroy()");
		super.onDestroy();
	}

	/**
	 * Notification from EngSpaQuiz that the user has moved up to
	 * the next level.
	 */
	@Override // QuizEventListener
	public void onNewLevel() {
		/*
		 * userLevel can be incremented by EngSpaQuiz when user answered
		 * enough questions, or set by user invoking options menu item
		 * UserDialog at any time.
		 * EngSpaQuiz ->
		 * 		[updates engSpaUser.level]
		 * 		EngSpaFragment.onNewLevel() [I/F QuizEventListener]
		 * 
		 * UserDialog ->
		 * 		MainActivity.onUserUpdate() [I/F UserSettingsListener] ->
		 * 			EngSpaFragment.setUser() ->
		 * 				EngSpaFragment.onNewLevel() [if level changed]
		 */
		if (BuildConfig.DEBUG) Log.d(engSpaActivity.getTag(),
				"EngSpaFragment.onNewLevel(" +
				engSpaUser.getUserLevel() + ")");
		showButtonLayout();
		showUserLevel();
		askQuestion(true);
	}
	public EngSpaUser getEngSpaUser() {
		return this.engSpaUser;
	}
	public EngSpaDAO getEngSpaDAO() {
		return this.engSpaDAO;
	}
	/**
	 * Create or update engSpaUser.
	 * @return false if no changes made
	 */
	public boolean setUser(String userName, int userLevel,
			QAStyle qaStyle) {
		userLevel = engSpaQuiz.validateUserLevel(userLevel);
		if (engSpaUser != null &&
				engSpaUser.getUserName().equals(userName) &&
				engSpaUser.getUserLevel() == userLevel &&
				engSpaUser.getQAStyle() == qaStyle) {
			this.statusTextView.setText("no changes made to user");
			return false;
		}
		boolean newLevel = true;
		if (engSpaUser == null) { // i.e. new user
			this.engSpaUser = new EngSpaUser(userName,
					userLevel, qaStyle);
			engSpaDAO.insertUser(engSpaUser);
		} else { // update to existing user
			newLevel = engSpaUser.getUserLevel() != userLevel;
			engSpaUser.setUserName(userName);
			engSpaUser.setUserLevel(userLevel);
			engSpaUser.setQAStyle(qaStyle);
			engSpaDAO.updateUser(engSpaUser);
		}
		userNameTextView.setText(engSpaUser.getUserName());
		if (newLevel) {
			getEngSpaQuiz().setUserLevel(userLevel);
		}
		onNewLevel(); // strictly only necessary if change level or qaStyle
		return true;
	}
	public void setUserQAStyle(QAStyle qaStyle) {
		engSpaUser.setQAStyle(qaStyle);
		onNewLevel();
	}
	public void setTopic(String topic) {
		if (topic == null) {
			showUserLevel();
		} else {
			this.engSpaActivity.setEngSpaTitle(topic);
		}
		this.engSpaQuiz.setTopic(topic);
		askQuestion(true);
	}

	@Override // QuizEventListener
	public void onTopicComplete() {
		showUserLevel();
		engSpaActivity.showTopicDialog();
	}
	private void showUserLevel() {
		String userLevelStr = isUserLevelAll() ? "ALL" :
				Integer.toString(engSpaUser.getUserLevel());
		this.engSpaActivity.setEngSpaTitle(this.levelStr + " " +
				userLevelStr);
	}
}
