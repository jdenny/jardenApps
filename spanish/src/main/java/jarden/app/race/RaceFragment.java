package jarden.app.race;

import com.jardenconsulting.spanishapp.BuildConfig;
import com.jardenconsulting.spanishapp.EngSpaActivity;
import com.jardenconsulting.spanishapp.MainActivity;
import com.jardenconsulting.spanishapp.R;

import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.NumbersQuiz;
import jarden.quiz.Quiz;
import jarden.timer.Timer;
import jarden.timer.TimerListener;
import android.app.Activity;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class RaceFragment extends Fragment implements TimerListener,
		OnClickListener, OnEditorActionListener, OnLongClickListener {
	//!! private int mode; // see QuizRaceIF
	private static final int CHASER_DELAY_TENTHS = 100;
	/*!!
	private static final long[] WRONG_VIBRATE = {
		0, 200, 200, 200
	};
	private static final long[] LOST_VIBRATE = {
		0, 400, 400, 400, 400, 400
	};
	*/
	// these variables don't change once setup in onCreateView:
	private LaneView laneBView;
	private LaneView myLaneView;
	//!! private LaneView opponentLaneView;
	private TextView levelBView;
	private TextView myLevelView;
	private EditText answerEditText;
	//!! private TextView opponentLevelView;
	private TextView statusTextView;
	// these variables change their values during the game:
	private Timer timer;
	/*!! no bluetooth - yet!
	private BluetoothService bluetoothService;
	*/
	private GameData gameData = new GameData();
	//!! private boolean clientMode = false;
	private int laneCols;
	private int raceLevel = 1;
	private Quiz quiz = new NumbersQuiz();
	/*!!
	private Vibrator vibrator;
	private SoundPool soundPool;
	private int soundError;
	private int soundLost;
	*/
	private EngSpaActivity engSpaActivity;
	
	@Override // Fragment
	public void onAttach(Activity activity) {
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "RaceFragment.onAttach()");
		super.onAttach(activity);
		this.engSpaActivity = (EngSpaActivity) activity;
	}
	@SuppressWarnings("deprecation")
	@Override // Fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "RaceFragment.onCreate(" +
				(savedInstanceState==null?"":"not ") + "null)");
		/*!!
		this.vibrator = (Vibrator) getActivity().getSystemService(
				FragmentActivity.VIBRATOR_SERVICE);
		this.soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		Activity activity = getActivity();
		this.soundError = soundPool.load(activity, R.raw.error, 1);
		this.soundLost = soundPool.load(activity, R.raw.lost, 1);
		 */
		setRetainInstance(true);
	}

	@Override  // Fragment
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "RaceFragment.onCreateView()");
		Resources res = getResources();
		this.laneCols = res.getInteger(R.integer.laneCols);
		View view = inflater.inflate(R.layout.quizrace_layout, container, false);
		LaneView laneAView = (LaneView) view.findViewById(R.id.laneA);
		laneAView.setBitmapId(R.drawable.blue_man);
		TextView levelAView = (TextView) view.findViewById(R.id.laneALevel);
		laneBView = (LaneView) view.findViewById(R.id.laneB);
		laneBView.setBitmapId(R.drawable.red_man);
		levelBView = (TextView) view.findViewById(R.id.laneBLevel);
		/*!!
		LaneView laneCView = (LaneView) view.findViewById(R.id.laneC);
		laneCView.setBitmapId(R.drawable.green_man);
		TextView levelCView = (TextView) view.findViewById(R.id.laneCLevel);
		*/
		this.statusTextView = (TextView) view.findViewById(R.id.statusTextView);
		this.answerEditText = (EditText) view.findViewById(R.id.answerEditText);
		this.answerEditText.setOnEditorActionListener(this);
		Button button = (Button) view.findViewById(R.id.resetButton);
		button.setOnClickListener(this);
		button.setOnLongClickListener(this);
		/*!!
		if (clientMode) {
			myLaneView = laneCView;
			opponentLaneView = laneAView;
			myLevelView = levelCView;
			opponentLevelView = levelAView;
		} else { */
			myLaneView = laneAView;
			myLevelView = levelAView;
			//!! opponentLaneView = laneCView;
			//!! opponentLevelView = levelCView;
		//!! }
		/*!!
		getActivity().getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		 */
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		nextQuestion();
		return view;
	}
	@Override // Fragment
	public void onResume() {
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "RaceFragment.onResume()");
		super.onResume();
		if (isRunning()) {
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
				/*!!
				vibrator.vibrate(WRONG_VIBRATE, -1);
				soundPool.play(soundError, 1.0f, 1.0f, 0, 0, 1.5f);
				*/
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
					status = getResources().getString(R.string.correct);
					onRightAnswer(); // move player in lane
				} else { // result must be FAIL
					engSpaActivity.onWrongAnswer();
					/*!!
					vibrator.vibrate(WRONG_VIBRATE, -1);
					soundPool.play(soundLost, 1.0f, 1.0f, 0, 0, 1.5f);
					*/
					status = " Answer: " + quiz.getCorrectAnswer();
				}
				nextQuestion();
			}
			this.statusTextView.setText(status);
			return true;
		}
		return false;
	}
	
    @Override // OnLongClickListener
    public boolean onLongClick(View view) {
		int id = view.getId();
		if (id == R.id.resetButton) {
			this.statusTextView.setText(R.string.resetButtonTip);
			return true;
		}
		return false;
    }
	@Override // Fragment
	public void onPause() {
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "RaceFragment.onPause()");
		super.onPause();
		if (this.timer != null) {
			timer.stop();
		}
	}

	@Override // Fragment
	public void onStop() {
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "RaceFragment.onStop()");
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "RaceFragment.onDestroy()");
		super.onDestroy();
	}

	public void reset() {
		// from QAFragment:
		this.raceLevel -= 2;
		if (raceLevel < 1)
			raceLevel = 1;
		answerEditText.setText("");
		answerEditText.requestFocus();
		nextQuestion();

		// from RaceFragment:
		// return if reset() called before onCreateView()
		if (myLevelView == null) {
			if (BuildConfig.DEBUG) {
				Log.w(MainActivity.TAG,
						"RaceFragment.reset() called before onCreateView()");
			}
			return;
		}
		gameData = new GameData();
		setLevel(raceLevel);
		myLaneView.setStatus(GameData.RUNNING);
		myLaneView.reset();
		laneBView.reset();
		//!! opponentLaneView.reset();
		startTimer();
	}
	private void nextQuestion() {
		try {
			String spanish = quiz.getNextQuestion(raceLevel);
			answerEditText.setText("");
			poseQuestion(spanish);
		} catch (EndOfQuestionsException e) {
			this.statusTextView.setText(e.getMessage());
		}
	}
	
	public void onRightAnswer() {
		if (this.gameData.status != GameData.CAUGHT) {
			int myPos = myLaneView.moveOn();
			if (myPos >= this.laneCols) {
				myLaneView.reset();
				laneBView.reset();
				logMessage("well done!");
				setLevel(raceLevel + 1);
				timer.setInterval(getCurrentBaddySleep());
			}
			gameData.position = myLaneView.getPosition();
			//!! transmitData(gameData);
		}
	}

	private boolean isRunning() {
		return this.gameData != null && this.gameData.status == GameData.RUNNING;
	}
	
	private void startTimer() {
		if (this.timer != null) {
			timer.stop();
		}
		this.gameData.status = GameData.RUNNING;
		this.timer = new Timer(this, getCurrentBaddySleep());
	}

	@Override
	public void onTimerEvent() {
    	// check to see if game already over before we
		// create new runnable object:
    	int himPos = laneBView.getPosition();
    	if (himPos >= laneCols) return;
		// run code within UI thread
		laneBView.post(new Runnable() {
            public void run() {
				int himPos = laneBView.moveOn();
				if (himPos >= laneCols) {
					timer.stop();
					myLaneView.setStatus(GameData.CAUGHT);
					gameData.status = GameData.CAUGHT;
					//!! transmitData(gameData);
					//!! mainActivity.onLost();
					/*!!
					vibrator.vibrate(LOST_VIBRATE, -1);
					soundPool.play(soundLost, 1.0f, 1.0f, 0, 0, 1.5f);
					*/
					engSpaActivity.onLost();

				}
            }
        });
	}

	/*!! no bluetooth yet
	private void transmitData(GameData gameData) {
		if (this.mode == MainActivity.BLUETOOTH_MODE) {
			byte[] data = new byte[3];
			data[0] = (byte) gameData.position;
			data[1] = (byte) gameData.level;
			data[2] = (byte) gameData.status;
	        if (this.bluetoothService.getState() == BluetoothService.BTState.connected) {
				bluetoothService.write(data);
	        } else {
	            Toast.makeText(mainActivity, "Not connected", Toast.LENGTH_LONG).show();
	        }
		}
	}
	*/

	private void logMessage(String message) {
		this.statusTextView.setText(message);
		if (BuildConfig.DEBUG) {
			Log.d(MainActivity.TAG, message);
		}
	}
	
	private void setLevel(int level) {
		String levelStr = String.valueOf(level);
		myLevelView.setText(levelStr);
		levelBView.setText(levelStr);
		gameData.level = level;
		raceLevel = level;
	}

	private int getCurrentBaddySleep() {
		return (int) (CHASER_DELAY_TENTHS /
				(1 + 0.2 * this.raceLevel));
	}
	private void poseQuestion(String question) {
		this.engSpaActivity.speakSpanish(question);
	}


	/*!!
	public void setMode(int mode) {
		this.mode = mode;
	}
	*/

	/*!! no bluetooth yet
	public void setBluetoothService(BluetoothService bluetoothService) {
		this.bluetoothService = bluetoothService;
	}

	public void onMessageRead(byte[] data) {
		GameData gameData = new GameData(data[0], data[1], data[2]);
		opponentLaneView.setData(gameData);
		opponentLevelView.setText(String.valueOf(gameData.level));
	}

	public void setClientMode(boolean clientMode) {
		this.clientMode = clientMode;
	}
	*/
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
