package jarden.app.race;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jardenconsulting.spanishapp.BuildConfig;
import com.jardenconsulting.spanishapp.EngSpaActivity;
import com.jardenconsulting.spanishapp.R;

import jarden.engspa.EngSpaUser;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.NumbersQuiz;
import jarden.quiz.Quiz;
import jarden.timer.Timer;
import jarden.timer.TimerListener;

public class RaceFragment extends Fragment implements TimerListener,
		OnClickListener, OnEditorActionListener, OnLongClickListener {
	public static final String TAG = "RaceFragment";
	private static final int CHASER_DELAY_TENTHS = 100;
	// these variables don't change once setup in onCreateView:
	private LaneView laneBView;
	private LaneView myLaneView;
	private TextView levelBView;
	private TextView myLevelView;
	private EditText answerEditText;
	private Timer timer;
	private int laneCols;
	private int raceLevel;
	private boolean caught = false;
	private Quiz quiz = new NumbersQuiz();
	private EngSpaActivity engSpaActivity;
	private EngSpaUser engSpaUser;

	@SuppressWarnings("deprecation")
	@Override // Fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
				(savedInstanceState==null?"":"not ") + "null)");
		setRetainInstance(true);
	}

	@Override // Fragment
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) Log.d(TAG, "onCreateView()");
		Activity activity = getActivity();
		this.engSpaActivity = (EngSpaActivity) activity;
		Resources res = getResources();
		this.laneCols = res.getInteger(R.integer.laneCols);
		View view = inflater.inflate(R.layout.fragment_race, container, false);
		LaneView laneAView = view.findViewById(R.id.laneA);
		laneAView.setBitmapId(R.drawable.blue_man);
		TextView levelAView = view.findViewById(R.id.laneALevel);
		laneBView = view.findViewById(R.id.laneB);
		laneBView.setBitmapId(R.drawable.red_man);
		levelBView = view.findViewById(R.id.laneBLevel);
		this.answerEditText = view.findViewById(R.id.answerEditText);
		this.answerEditText.setOnEditorActionListener(this);
		Button button = view.findViewById(R.id.resetButton);
		button.setOnClickListener(this);
		button.setOnLongClickListener(this);
		myLaneView = laneAView;
		myLevelView = levelAView;
		this.engSpaUser = this.engSpaActivity.getEngSpaUser();
		this.raceLevel = this.engSpaUser.getRaceLevel();
		showLevel();
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
				Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        this.engSpaActivity.setTip(R.string.Numbers_Game);
		nextQuestion();
		return view;
	}
	@Override // Fragment
	public void onResume() {
		if (BuildConfig.DEBUG) Log.d(TAG, "onResume()");
		super.onResume();
		if (!this.caught) {
			startTimer();
		}
	}

	@Override // OnEditorActionListener
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			String answer = answerEditText.getText().toString().trim();
			if (answer.length() == 0) {
	    		poseQuestion(quiz.getCurrentQuestion());
				return true;
			}
			String status;
			int result = quiz.isCorrect(answer);
			if (result == Quiz.INCORRECT) {
				engSpaActivity.onWrongAnswer();
				status = answer + " " + getResources().getString(R.string.incorrect);
				String hint = quiz.getHint();
				if (hint != null && hint.length() > 0) {
					answerEditText.setText(hint);
					answerEditText.setSelection(hint.length()); // put cursor at end
				} else {
					answerEditText.setText("");
				}
				poseQuestion(quiz.getCurrentQuestion());
			} else {
				if (result == Quiz.CORRECT) {
					status = getResources().getString(R.string.right);
					onRightAnswer(); // move player in lane
				} else { // result must be FAIL
					engSpaActivity.onWrongAnswer();
					status = " Answer: " + quiz.getCorrectAnswer();
				}
				nextQuestion();
			}
			this.engSpaActivity.setStatus(status);
			return true;
		}
		return false;
	}
	
    @Override // OnLongClickListener
    public boolean onLongClick(View view) {
		int id = view.getId();
		if (id == R.id.resetButton) {
			this.engSpaActivity.setTip(R.string.resetButtonTip);
			return true;
		}
		return false;
    }
	@Override // Fragment
	public void onPause() {
		if (BuildConfig.DEBUG) Log.d(TAG, "onPause()");
		super.onPause();
		if (this.timer != null) {
			timer.stop();
		}
	}

	@Override // Fragment
	public void onStop() {
		if (BuildConfig.DEBUG) Log.d(TAG, "onStop()");
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		if (BuildConfig.DEBUG) Log.d(TAG, "onDestroy()");
		super.onDestroy();
	}

	private void reset() {
		this.raceLevel -= 2;
		if (raceLevel < 1)
			raceLevel = 1;
		this.engSpaUser.setRaceLevel(raceLevel);
		answerEditText.setText("");
		answerEditText.requestFocus();
		nextQuestion();

		if (myLevelView == null) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "reset() called before onCreateView()");
			}
			return;
		}
		setLevel(raceLevel);
		myLaneView.setCaught(false);
		myLaneView.reset();
		laneBView.reset();
		startTimer();
	}
	private void nextQuestion() {
		try {
			String spanish = quiz.getNextQuestion(raceLevel);
			answerEditText.setText("");
			poseQuestion(spanish);
		} catch (EndOfQuestionsException e) {
			Log.e(TAG, "nextQuestion(); endOfQuestionsException");
			this.engSpaActivity.setStatus("unexpected end of questions!");
		}
	}
	
	public void onRightAnswer() {
		if (!this.caught) {
			int myPos = myLaneView.moveOn();
			if (myPos >= this.laneCols) {
				myLaneView.reset();
				laneBView.reset();
				setLevel(raceLevel + 1);
				timer.setInterval(getCurrentBaddySleep());
			}
		}
	}
	private void startTimer() {
		if (this.timer != null) {
			timer.stop();
		}
		this.caught = false;
		this.timer = new Timer(this, getCurrentBaddySleep());
	}
	@Override // TimerListener
	public void onTimerEvent() {
    	// check to see if game already over before we
		// create new runnable object:
    	if (laneBView.getPosition() >= laneCols) return;
		// run code within UI thread
		laneBView.post(new Runnable() {
            public void run() {
                if (laneBView.moveOn() >= laneCols) {
                    timer.stop();
					myLaneView.setCaught(true);
					caught = true;
                    engSpaActivity.onLost();
                }
            }
        });
	}
	private void setLevel(int level) {
		this.raceLevel = level;
		this.engSpaUser.setRaceLevel(raceLevel);
		showLevel();
	}
	private void showLevel() {
		String levelStr = String.valueOf(this.raceLevel);
		myLevelView.setText(levelStr);
		levelBView.setText(levelStr);

	}
	private int getCurrentBaddySleep() {
		return (int) (CHASER_DELAY_TENTHS /
				(1 + 0.2 * this.raceLevel));
	}
	private void poseQuestion(String question) {
		this.engSpaActivity.speakSpanish(question);
	}
	@Override // OnClickListener
	public void onClick(View view) {
		int viewId = view.getId();
    	if (viewId == R.id.resetButton) {
    		quiz.reset();
    		this.reset();
    	} else {
	        Toast.makeText(getActivity(), "unknown button pressed: " + view,
	        		Toast.LENGTH_LONG).show();
    	}
	}
}
