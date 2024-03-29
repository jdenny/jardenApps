package com.jardenconsulting.spanishapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import androidx.fragment.app.Fragment;
import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaQuiz;
import jarden.engspa.EngSpaQuiz.QuizMode;
import jarden.engspa.EngSpaUser;
import jarden.provider.engspa.EngSpaContract.QAStyle;
import jarden.provider.engspa.EngSpaContract.VoiceText;
import jarden.quiz.EndOfQuestionsException;

public class EngSpaFragment extends Fragment implements OnClickListener,
		OnLongClickListener, OnEditorActionListener {
	public static final String TAG = "EngSpaFragment";
	private static final int PHRASE_ACTIVITY_CODE = 1002;

	private TextView statsTextView;
    private TextView questionTextView;
	private TextView attributeTextView;
	private EditText answerEditText;
	private ViewGroup selfMarkLayout;
	private ViewGroup buttonLayout;
	private ImageButton micButton;
    private int tipResId;

	private Random random = new Random();
	private int spanishRed;
	private int spanishBlue;
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
    private Button clearAnswerButton;

	@Override // Fragment
	public void onAttach(Context context) {
		if (BuildConfig.DEBUG) Log.d(TAG, "onAttach()");
		super.onAttach(context);
	}
	@SuppressWarnings("deprecation")
	@Override // Fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
				(savedInstanceState==null?"":"not ") + "null)");
		setRetainInstance(true);
		Resources resources = getResources();
		this.spanishRed = resources.getColor(R.color.spanishRed);
		this.spanishBlue = resources.getColor(R.color.spanishBlue);
	}
	@Override // Fragment
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG,
					"onCreateView(); question=" + question +
					"; savedInstanceState is " + (savedInstanceState==null?"":"not ") + "null");
		}
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
		this.statsTextView = rootView.findViewById(R.id.statsTextView);
        this.statsTextView.setOnLongClickListener(this);
		this.selfMarkLayout = rootView.findViewById(R.id.selfMarkLayout);
		this.buttonLayout = rootView.findViewById(R.id.buttonLayout);
		this.selfMarkLayout.setVisibility(View.GONE);
		Button button = rootView.findViewById(R.id.goButton);
		button.setOnClickListener(this);
		button.setOnLongClickListener(this);
        int goButtonHeight = button.getHeight();
		this.micButton = rootView.findViewById(R.id.micButton);
		this.micButton.setOnClickListener(this);
		this.micButton.setOnLongClickListener(this);
        this.micButton.setMaxHeight(goButtonHeight);
		button = rootView.findViewById(R.id.incorrectButton);
		button.setOnClickListener(this);
		button.setOnLongClickListener(this);
		button = rootView.findViewById(R.id.correctButton);
		button.setOnClickListener(this);
		button.setOnLongClickListener(this);
        this.clearAnswerButton = rootView.findViewById(R.id.clearAnswerButton);
        this.clearAnswerButton.setOnClickListener(this);
        this.clearAnswerButton.setOnLongClickListener(this);
        this.clearAnswerButton.setVisibility(View.GONE);
		this.questionTextView = rootView.findViewById(R.id.questionTextView);
		this.attributeTextView = rootView.findViewById(R.id.attributeTextView);
		this.answerEditText = rootView.findViewById(R.id.answerEditText);
		if (questionText != null) {
			this.questionTextView.setText(questionText);
			this.answerEditText.setText(pendingAnswer);
			this.attributeTextView.setText(attributeText);
			if (selfMarkLayoutVisibility == View.VISIBLE) showSelfMarkLayout();
		}
		this.answerEditText.setOnEditorActionListener(this);
		return rootView;
	}
	@Override // Fragment
    /*
    The design approach we've gone for: only access View elements
    before we get to onResume()
     */
	public void onResume() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onResume(); question=" + question);
		}
		super.onResume();
        this.engSpaActivity = (EngSpaActivity) getActivity();
        if (this.engSpaDAO == null) {
            this.engSpaDAO = engSpaActivity.getEngSpaDAO();
            this.engSpaUser = engSpaActivity.getEngSpaUser();
            this.engSpaQuiz = engSpaActivity.getEngSpaQuiz();
        }
        if (this.spanish == null) {
            askQuestion(true);
        } else {
            // in case user presses speaker button
            this.engSpaActivity.setSpanish(spanish);
            if (this.tipResId != 0) this.engSpaActivity.setTip(tipResId);
            showStats();
        }
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
			this.answerEditText.setBackgroundColor(spanishRed);
			this.questionTextView.setBackgroundColor(spanishBlue);
			this.answerEditText.setHint(R.string.spanishStr);
			this.micButton.setVisibility(View.VISIBLE);
		} else {
			this.answerEditText.setBackgroundColor(spanishBlue);
			this.questionTextView.setBackgroundColor(spanishRed);
			this.answerEditText.setHint(R.string.englishStr);
			this.micButton.setVisibility(View.INVISIBLE);
		}
		if (this.currentQAStyle.voiceText == VoiceText.voice) {
			this.questionTextView.setText("");
		} else {
			this.questionTextView.setText(this.question);
		}
        int helpId;
        if (getNext) {
            QuizMode quizMode = engSpaUser.getQuizMode();
            if (quizMode == QuizMode.PRACTICE) {
                helpId = R.string.Practice_Mode;
            } else if (quizMode == QuizMode.TOPIC) {
                helpId = R.string.Topic_Mode;
            } else { // must be LEARN
                helpId = R.string.Learn_Mode;
            }
        } else helpId = R.string.tryGoAgainTip;
        setTip(helpId);
		showStats();
	}
	private void showStats() {
		int fwct = engSpaQuiz.getFailedWordCount();
        int cwct = engSpaQuiz.getCurrentWordCount();
        StringBuilder sb = new StringBuilder("Level=");
        sb.append(Integer.toString(engSpaUser.getLearnLevel()));
        if (engSpaUser.getQuizMode() == QuizMode.LEARN) {
            sb.append(engSpaUser.isLearnModePhase2() ? 'B' : 'A');
        }
        //?? sb.append("; S=" + engSpaUser.getQAStyle().ordinal());
        if (cwct >= 0) sb.append(" Current=" + cwct);
        if (fwct >= 0) sb.append(" Fails=" + fwct);
		this.statsTextView.setText(sb.toString());
		if (BuildConfig.DEBUG) {
			String debugState = engSpaQuiz.getDebugState();
			Log.d(TAG, debugState);
		}
	}
	private void nextQuestion() {
		QuizMode quizMode = this.engSpaUser.getQuizMode();
		try {
			this.spanish = engSpaQuiz.getNextQuestion(
					engSpaActivity.getQuestionSequence());
		} catch (EndOfQuestionsException e) {
            int userLevel = this.engSpaUser.getLearnLevel();
            int maxUserLevel = this.engSpaDAO.getMaxUserLevel();
			if (quizMode == QuizMode.LEARN) {
                if (this.engSpaUser.isLearnModePhase2()) {
                    this.engSpaUser.setLearnModePhase2(false);
                    int newUserLevel = userLevel + 1;
                    if (newUserLevel > maxUserLevel) {
                        this.engSpaUser.setQAStyle(QAStyle.alternate);
                        startPracticeMode(R.string.levelsComplete);
                    } else {
                        engSpaQuiz.setUserLevel(newUserLevel);
                    }
                } else {
                    engSpaQuiz.unsetModeInitialised();
                    this.engSpaUser.setLearnModePhase2(true);
                }
            } else if (quizMode == QuizMode.PRACTICE) {
                startPracticeMode(R.string.endOfPracticeSet);
			} else { // must be topic mode
                if (userLevel < maxUserLevel) {
                    startLearnMode(R.string.endOfTopic);
                } else {
                    startPracticeMode(R.string.endOfTopic);
                }
			}
            try {
                this.spanish = engSpaQuiz.getNextQuestion(
                        engSpaActivity.getQuestionSequence());
            } catch (EndOfQuestionsException e1) {
                if (BuildConfig.DEBUG) Log.e(TAG, "nextQuestion() it's all gone wrong!");
                engSpaActivity.setStatus("end of exceptions error!");
            }
		}
		String english = engSpaQuiz.getEnglish();
		
		// get qaStyle:
		// 	if failed word: from question
		//  else if LEARN mode: qaStyle 2 or 3 depending on phase
		//  otherwise: from user
		this.currentQAStyle = engSpaQuiz.getQAStyleFromQuestion();
		if (this.currentQAStyle == null) { // i.e. if it's not a failed word
			if (quizMode == QuizMode.LEARN) {
				this.currentQAStyle = this.engSpaUser.isLearnModePhase2() ?
						QAStyle.writtenEngToSpa :
						QAStyle.spokenWrittenSpaToEng;
			} else {
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
		clearAnswerText();
	}
    private void startPracticeMode(int messageId) {
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_LONG).show();
        engSpaQuiz.setQuizMode(QuizMode.PRACTICE);
        engSpaActivity.setAppBarTitle();
    }
    private void startLearnMode(int messageId) {
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_LONG).show();
        engSpaQuiz.setQuizMode(QuizMode.LEARN);
        engSpaActivity.setAppBarTitle();
    }
    private void clearAnswerText() {
        this.answerEditText.getText().clear();
    }
	@Override // onClickListener
	public void onClick(View view) {
		engSpaActivity.setStatus(EngSpaActivity.CLEAR_STATUS);
        int id = view.getId();
		if (id == R.id.goButton) {
			goPressed();
		} else if (id == R.id.micButton) {
			startRecogniseSpeechActivity();
        } else if (id == R.id.correctButton) {
			selfMarkButton(true);
        } else if (id == R.id.incorrectButton) {
			selfMarkButton(false);
        } else if (id == R.id.clearAnswerButton) {
            clearAnswerText();
		}
	}
	@Override // OnLongClickListener
	public boolean onLongClick(View view) {
        this.engSpaActivity.showHelp();
		int id = view.getId();
		if (id == R.id.goButton) {
			setTip(R.string.goButtonTip);
			return true;
		} else if (id == R.id.correctButton) {
			setTip(R.string.correctButtonTip);
			return true;
		} else if (id == R.id.incorrectButton) {
			setTip(R.string.incorrectButtonTip);
			return true;
		} else if (id == R.id.micButton) {
			setTip(R.string.Mic_Button);
			return true;
        } else if (id == R.id.clearAnswerButton) {
            setTip(R.string.clearAnswerTip);
            return true;
        } else if (id == R.id.statsTextView) {
            setTip(R.string.statsTip);
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
		if writtenEngToSpa also speak Spanish
	 */
	private void goPressed() {
        this.clearAnswerButton.setVisibility(View.GONE);
		String suppliedAnswer = getSuppliedAnswer().trim();
		if (suppliedAnswer.length() == 0) {
			showSelfMarkLayout();
			this.answerEditText.setText(this.correctAnswer);
            if (this.currentQAStyle == QAStyle.writtenEngToSpa) {
                // speak Spanish answer
                this.engSpaActivity.speakSpanish(this.correctAnswer);
            }
			if (this.currentQAStyle.voiceText == VoiceText.voice) {
				// if question was spoken only, user may want to see the question
				String translated = this.currentQAStyle.spaAnswer ? engSpaQuiz
						.getEnglish() : engSpaQuiz.getSpanish();
				this.questionTextView.setText(translated);
			}
			setTip(R.string.Self_Mark);
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
            this.clearAnswerButton.setVisibility(isCorrect ? View.GONE : View.VISIBLE);
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
    /**
     * Called when EngSpaFragment first shown, or change to quizMode or learnLevel.
     */
	public void reset() {
		/*
		 * userLevel can be incremented by EngSpaQuiz when user answered
		 * enough questions, or set by user invoking options menu item
		 * UserDialog at any time.
		 * EngSpaQuiz ->
		 * 		[updates engSpaUser.level]
		 * 		EngSpaFragment.onNewLevel() [I/F QuizEventListener]
		 * 
		 * UserDialog ->
		 * 		MainActivity.onUserUpdate() [I/F IntValueListener] ->
		 * 			EngSpaFragment.setUser() ->
		 * 				EngSpaFragment.onNewLevel() [if level changed]
		 */
		if (BuildConfig.DEBUG) Log.d(TAG,
				"reset(" +
				engSpaUser.getLearnLevel() + ")");
		showButtonLayout();
		askQuestion(true);
	}
}
