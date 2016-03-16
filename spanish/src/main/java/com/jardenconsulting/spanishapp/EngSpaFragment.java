package com.jardenconsulting.spanishapp;

import jarden.provider.engspa.EngSpaContract.QAStyle;
import jarden.provider.engspa.EngSpaContract.VoiceText;
import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaQuiz;
import jarden.engspa.EngSpaQuiz.QuizEventListener;
import jarden.engspa.EngSpaUser;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Toast;

public class EngSpaFragment extends Fragment implements OnClickListener,
		OnLongClickListener, OnEditorActionListener, QuizEventListener {
	public static final String TAG = "EngSpaFragment";
	private static final int PHRASE_ACTIVITY_CODE = 1002;

	private TextView currentCtTextView;
	private TextView failCtTextView;private TextView questionTextView;
	private TextView attributeTextView;
	private EditText answerEditText;
	private ViewGroup selfMarkLayout;
	private ViewGroup buttonLayout;
	private ImageButton micButton;
	private String levelStr;
    private int tipResId;

	private Random random = new Random();
	private int red;
	private int blue;
	private QAStyle currentQAStyle;
	private String question;
	private String spanish;
	private String correctAnswer;
	private String responseIfCorrect;
	private EngSpaQuiz engSpaQuiz;
	private EngSpaUser engSpaUser;
	private EngSpaDAO engSpaDAO;
	private EngSpaActivity engSpaActivity;

	/**
	 * Used by QAStyle.alternate, which is used to alternate
	 * between 2 QAStyles: spokenSpaToEng and writtenEngToSpa.
	 * If firstAlternative is true, then the style used for the
	 * next question is the spokenSpaToEng, else writtenEngToSpa.
	 */
	private boolean firstAlternative;
    private String topic;

    @Override // Fragment
	public void onAttach(Context context) {
		if (BuildConfig.DEBUG) Log.d(TAG, "onAttach()");
		super.onAttach(context);
		//!! this.engSpaActivity = (EngSpaActivity) getActivity();
	}
	@SuppressWarnings("deprecation")
	@Override // Fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
				(savedInstanceState==null?"":"not ") + "null)");
		setRetainInstance(true);
		Resources resources = getResources();
		this.red = resources.getColor(R.color.samRed);
		this.blue = resources.getColor(R.color.samBlue);
		this.levelStr = resources.getString(R.string.levelStr);

		/*!
        this.engSpaDAO = engSpaActivity.getEngSpaDAO();
        this.engSpaUser = engSpaActivity.getEngSpaUser();
		this.engSpaQuiz = new EngSpaQuiz(engSpaDAO, this.engSpaUser);
		this.engSpaQuiz.setQuizEventListener(this);
        if (this.topic != null) setTopic2();
        */
	}
	@Override // Fragment
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG,
					"onCreateView(); question=" + question +
					"; savedInstanceState is " + (savedInstanceState==null?"":"not ") + "null");
		}
		//!! this.engSpaActivity = (EngSpaActivity) getActivity();
		// Potentially restore state after configuration change; before we re-create
		// the views, get relevant information from current values. See knowledgeBase.txt
		CharSequence questionText = null;
		CharSequence attributeText = null;
		CharSequence pendingAnswer = null;
		int selfMarkLayoutVisibility = View.GONE;
		if (questionTextView != null) {
			questionText = questionTextView.getText();
			attributeText = attributeTextView.getText();
			pendingAnswer = answerEditText.getText();
			selfMarkLayoutVisibility = selfMarkLayout.getVisibility();
		}
		// Now get new layout
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		this.currentCtTextView = (TextView) rootView.findViewById(R.id.currentCtTextView);
		this.failCtTextView = (TextView) rootView.findViewById(R.id.failCtTextView);
		failCtTextView.setOnLongClickListener(this);

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
		if (questionText != null) {
			this.questionTextView.setText(questionText);
			this.answerEditText.setText(pendingAnswer);
			this.attributeTextView.setText(attributeText);
			if (selfMarkLayoutVisibility == View.VISIBLE) showSelfMarkLayout();
		}
		this.answerEditText.setOnEditorActionListener(this);
        /*!!
		if (this.spanish == null) {
			askQuestion(true);
			showUserLevel();
		} else {
            // in case user presses speaker button
            this.engSpaActivity.setSpanish(spanish);
			showStats();
		}
        setAppBarTitle2(this.engSpaQuiz.getTopic());
        */
		return rootView;
	}
	@Override // Fragment
    /*
    The design approach we've gone for: only access View elements
    before we get to onResume()
     */
	public void onResume() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onResume(); question=" + question
            );
		}
		super.onResume();
        this.engSpaActivity = (EngSpaActivity) getActivity();
        if (this.engSpaDAO == null) {
            this.engSpaDAO = engSpaActivity.getEngSpaDAO();
            this.engSpaUser = engSpaActivity.getEngSpaUser();
            this.engSpaQuiz = new EngSpaQuiz(engSpaDAO, this.engSpaUser);
            this.engSpaQuiz.setQuizEventListener(this);
            if (this.topic != null) setTopic2();
        }
        if (this.spanish == null) {
            askQuestion(true);
            showUserLevel();
        } else {
            // in case user presses speaker button
            this.engSpaActivity.setSpanish(spanish);
            speakSpanishIfRequired();
            if (this.tipResId != 0) this.engSpaActivity.setTip(tipResId);
            showStats();
        }
        setAppBarTitle2(this.engSpaQuiz.getTopic());

        /*!!
        if (this.spanish != null) {
            speakSpanishIfRequired();
            //!! showStats(); yes or no?
        }
        */
	}
    private void speakSpanishIfRequired() {
        if (this.currentQAStyle.voiceText != VoiceText.text) {
            this.engSpaActivity.speakSpanish(this.spanish);
        }
    }
	/**
	 * if getNext is true: get next question
	 * askQuestion using UI (textFields & voice)
	 */
	private void askQuestion(boolean getNext) {
		if (getNext) {
			nextQuestion();
            this.engSpaActivity.setSpanish(this.spanish);
		}
		String hint = engSpaQuiz.getHint(!this.currentQAStyle.spaQuestion);
		if (hint.length() > 0) hint = "hint: " + hint;
		this.attributeTextView.setText(hint);
        speakSpanishIfRequired();
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
        // TODO: fix this!
        // if (!isCorrect) /*!!engSpaActivity.*/setTip(R.string.tryGoAgainTip);
		/*!!engSpaActivity.*/setTip(R.string.engSpaTip);
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
			Log.d(TAG, debugState);
		}
	}
	private void nextQuestion() {
		this.spanish = engSpaQuiz.getNextQuestion2(
				engSpaActivity.getQuestionSequence());
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
		this.responseIfCorrect = "";
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
		// TODO: turn on show help if not already on
		int id = view.getId();
		if (id == R.id.goButton) {
			/*!!engSpaActivity.*/setTip(R.string.goButtonTip);
			return true;
		} else if (id == R.id.correctButton) {
			/*!!engSpaActivity.*/setTip(R.string.correctButtonTip);
			return true;
		} else if (id == R.id.incorrectButton) {
			/*!!engSpaActivity.*/setTip(R.string.incorrectButtonTip);
			return true;
		} else if (id == R.id.micButton) {
			/*!!engSpaActivity.*/setTip(R.string.micButtonTip);
			return true;
		}
		return false;
	}
	@Override // Fragment
	public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, "onPause()");
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
			Log.d(TAG,
					"onActivityResult(resultCode=" + resultCode);
		}
		if (resultCode == Activity.RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			for (String match: matches) {
				Log.d(TAG, "match=" + match);
				if (match.equalsIgnoreCase(this.correctAnswer)) {
					this.responseIfCorrect = "Right! " + match;
					setIsCorrect(true);
					return;
				}
			}
			this.answerEditText.setText(matches.get(0));
            this.engSpaActivity.onWrongAnswer();
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"resultCode from speech recognition: " + resultCode);
			}
			engSpaActivity.setStatus(R.string.speechRecognitionError);
		}
	}
	private String getSuppliedAnswer() {
		return this.answerEditText.getText().toString().trim();
	}
	private void selfMarkButton(boolean isCorrect) {
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
			/*!!engSpaActivity.*/setTip(R.string.selfMarkTip);
		} else {
			String normalisedCorrectAnswer = normalise(this.correctAnswer);
			String normalisedSuppliedAnswer = normalise(suppliedAnswer);
			boolean isCorrect = normalisedCorrectAnswer.equals(normalisedSuppliedAnswer);
			if (!isCorrect && this.currentQAStyle.spaAnswer) {
				int res = EngSpaQuiz.compareSpaWords(normalisedCorrectAnswer,
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
    private void setTip(int tipResId) {
        this.tipResId = tipResId;
        this.engSpaActivity.setTip(tipResId);
    }
	private void setIsCorrect(boolean isCorrect) {
		engSpaQuiz.setCorrect(isCorrect, currentQAStyle);
		if (isCorrect) {
			if (this.responseIfCorrect.length() > 0) {
				engSpaActivity.setStatus(this.responseIfCorrect);
				Toast.makeText(getActivity(), responseIfCorrect, Toast.LENGTH_LONG).show();
			}
		} else {
            this.engSpaActivity.onWrongAnswer();
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
		// TODO: and sort out ? and upside-down ? & !
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
		if (BuildConfig.DEBUG) Log.d(TAG, "onDestroy()");
		super.onDestroy();
	}
    public void newUserLevel() {
        this.engSpaQuiz.setUserLevel(this.engSpaUser.getUserLevel());
        onNewLevel();
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
		if (BuildConfig.DEBUG) Log.d(TAG,
				"onNewLevel(" +
				engSpaUser.getUserLevel() + ")");
		showButtonLayout();
		showUserLevel();
		askQuestion(true);
	}
	public void setTopic(String topic) {
        this.topic = topic;
        if (this.answerEditText != null) {
            // i.e. if already done onCreateView()
            setTopic2();
        }
	}
    private void setTopic2() {
        setAppBarTitle2(topic);
        this.engSpaQuiz.setTopic(topic);
        //!! askQuestion(true); // I don't think we need this, but just check
    }
	private void setAppBarTitle2(String topic) {
		if (topic == null) {
			showUserLevel();
		} else {
			this.engSpaActivity.setAppBarTitle(topic);
		}
	}
	@Override // QuizEventListener
	public void onTopicComplete() {
		showUserLevel();
		engSpaActivity.showTopicDialog();
	}
	private void showUserLevel() {
		String userLevelStr = isUserLevelAll() ? "ALL" :
				Integer.toString(engSpaUser.getUserLevel());
		this.engSpaActivity.setAppBarTitle(this.levelStr + " " +
				userLevelStr);
	}
}
