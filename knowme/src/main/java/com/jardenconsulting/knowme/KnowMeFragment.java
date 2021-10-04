package com.jardenconsulting.knowme;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jardenconsulting.bluetooth.BluetoothService;

import java.io.InputStream;

import jarden.knowme.ActionName;
import jarden.knowme.EndOfQuestionsException;
import jarden.knowme.KnowMeService;
import jarden.knowme.QAResults;
import jarden.knowme.QuestionManager;

public class KnowMeFragment extends Fragment implements
		OnCheckedChangeListener, OnClickListener {
	private TextView myQuestionView;
	private TextView hisQuestionView;
	private RadioGroup myRadioGroup;
	private RadioGroup hisRadioGroup;
	private Button goButton;
	private KnowMeActivityIF knowMeActivity;

	private boolean btServerMode = true; // turned off if we connect to remote
											// server
	// if bluetooth is not available (e.g. on device emulator), run both
	// users on a single device
	private boolean singleDeviceMode = false;
	private KnowMeService knowMeService;
	private int percentCorrect;
	private BluetoothService bluetoothService;
	private RadioButton hisRadioButton1;
	private RadioButton hisRadioButton2;
	private RadioButton hisRadioButton3;
	private RadioButton hisRadioButton4;
	private RadioButton myRadioButton1;
	private RadioButton myRadioButton2;
	private RadioButton myRadioButton3;
	private RadioButton myRadioButton4;
	private String[] questionArray;
	private boolean smallScreen;
	private String waitingTemplate;
	private String waitingStr;
	private String playerName;
	private String otherPlayerName;
	private String opt1, opt2, opt3, opt4;

	@SuppressLint("ResourceType")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_knowme, container, false);
		Resources resources = getResources();
		this.opt1 = resources.getString(R.string.opt1) + ". ";
		this.opt2 = resources.getString(R.string.opt2) + ". ";
		this.opt3 = resources.getString(R.string.opt3) + ". ";
		this.opt4 = resources.getString(R.string.opt4) + ". ";
		int screenSize = resources.getConfiguration().screenLayout &
		        Configuration.SCREENLAYOUT_SIZE_MASK;
		this.waitingTemplate = resources.getString(R.string.waitingTemplate);
		this.smallScreen = (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL ||
				screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL);
		this.knowMeActivity = (KnowMeActivityIF) getActivity();
		this.myQuestionView = view.findViewById(R.id.myQuestionView);
		this.hisQuestionView = view.findViewById(R.id.hisQuestionView);
		this.myRadioButton1 = view.findViewById(R.id.myRadioButton1);
		this.myRadioButton2 = view.findViewById(R.id.myRadioButton2);
		this.myRadioButton3 = view.findViewById(R.id.myRadioButton3);
		this.myRadioButton4 = view.findViewById(R.id.myRadioButton4);
		this.hisRadioButton1 = view.findViewById(R.id.hisRadioButton1);
		this.hisRadioButton2 = view.findViewById(R.id.hisRadioButton2);
		this.hisRadioButton3 = view.findViewById(R.id.hisRadioButton3);
		this.hisRadioButton4 = view.findViewById(R.id.hisRadioButton4);
		this.myRadioButton1.setId(1);
		this.myRadioButton2.setId(2);
		this.myRadioButton3.setId(3);
		this.myRadioButton4.setId(4);
		this.hisRadioButton1.setId(1);
		this.hisRadioButton2.setId(2);
		this.hisRadioButton3.setId(3);
		this.hisRadioButton4.setId(4);
		this.myRadioGroup = view.findViewById(R.id.myRadioGroup);
		this.myRadioGroup.setOnCheckedChangeListener(this);
		this.hisRadioGroup = view.findViewById(R.id.hisRadioGroup);
		this.hisRadioGroup.setOnCheckedChangeListener(this);
		goButton = view.findViewById(R.id.goButton);
		goButton.setOnClickListener(this);
		InputStream is = getResources().openRawResource(R.raw.questions);
		QuestionManager qm = QuestionManager.getInstance();
		qm.loadQuestions(is);
		return view;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (myRadioGroup.getCheckedRadioButtonId() != 0
				&& hisRadioGroup.getCheckedRadioButtonId() != 0) {
			goButton.setEnabled(true);
		}
	}

	public void nextQuestion() {
		if (this.btServerMode) {
			try {
				this.questionArray =
						knowMeService.getNextQuestion(playerName);
				poseQuestion();
				if (this.singleDeviceMode) {
					// simulate other player getting next question:
					knowMeService.getNextQuestion(this.otherPlayerName);
				}
			} catch (EndOfQuestionsException e) {
				endOfQuestions();
			}
		} else {
			sendBTMessage(ActionName.NEXT_QUESTION, null);
		}
	}
	public void nextQuiz() throws EndOfQuestionsException {
		if (this.btServerMode) {
			this.questionArray =
					knowMeService.getNextQuiz10(playerName);
			poseQuestion();
		} else {
			sendBTMessage(ActionName.NEXT_QUIZ, null);
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		knowMeActivity.setStatusMessage("");
		if (id == R.id.goButton) {
			int myMe = myRadioGroup.getCheckedRadioButtonId();
			int myHim = hisRadioGroup.getCheckedRadioButtonId();
			this.goButton.setEnabled(false);
			knowMeActivity.setStatusMessage(this.waitingStr);
			if (this.btServerMode) {
				boolean bothAnswered = this.knowMeService.setMyAnswers(
						playerName, myMe, myHim);
				if (bothAnswered) {
					if (this.singleDeviceMode) {
						swapPlayerNames();
					}
					getAndDisplayResults();
				} else {
					if (this.singleDeviceMode) {
						swapPlayerNames();
						String[] questionArrayMe = this.questionArray;
						this.questionArray =
								knowMeService.getCurrentQuestion(this.playerName);
						poseQuestion();
						this.questionArray = questionArrayMe;
					}
				}
			} else {
				String message = "&myme=" + myMe + "&myhim=" + myHim;
				sendBTMessage(ActionName.ANSWER, message);
			}
		} else {
			throw new IllegalStateException("unrecognised button clicked: "
					+ view);
		}
	}
	// can only happen in singlePlayerMode.
	private void swapPlayerNames() {
		String name = this.playerName;
		this.playerName = this.otherPlayerName;
		this.otherPlayerName = name;
		knowMeActivity.setOtherPlayerName(this.otherPlayerName);
	}

	private void poseQuestion() {
		this.myRadioGroup.clearCheck();
		this.hisRadioGroup.clearCheck();
		this.myQuestionView.setText(this.questionArray[0]);
		this.hisQuestionView.setText(questionArray[1]);
		String text;
		if (this.smallScreen) {
		    text = this.opt1 + questionArray[2];
			this.hisRadioButton1.setText(text);
			text = this.opt2 + questionArray[3];
			this.hisRadioButton2.setText(text);
		} else {
			this.hisRadioButton1.setText(questionArray[2]);
			this.myRadioButton1.setText(questionArray[2]);
			this.hisRadioButton2.setText(questionArray[3]);
			this.myRadioButton2.setText(questionArray[3]);
		}
		if (questionArray.length > 4) {
			if (this.smallScreen) {
			    text = this.opt3 + questionArray[4];
				this.hisRadioButton3.setText(text);
			} else {
				this.hisRadioButton3.setText(questionArray[4]);
				this.myRadioButton3.setText(questionArray[4]);
			}
			this.hisRadioButton3.setVisibility(View.VISIBLE);
			this.myRadioButton3.setVisibility(View.VISIBLE);
		} else {
			this.hisRadioButton3.setVisibility(View.GONE);
			this.myRadioButton3.setVisibility(View.GONE);
		}
		if (questionArray.length > 5) {
			if (this.smallScreen) {
			    text = this.opt4 + questionArray[5];
				this.hisRadioButton4.setText(text);
			} else {
				this.hisRadioButton4.setText(questionArray[5]);
				this.myRadioButton4.setText(questionArray[5]);
			}
			this.hisRadioButton4.setVisibility(View.VISIBLE);
			this.myRadioButton4.setVisibility(View.VISIBLE);
		} else {
			this.hisRadioButton4.setVisibility(View.GONE);
			this.myRadioButton4.setVisibility(View.GONE);
		}
		this.goButton.setEnabled(false);
		knowMeActivity.questionPosed();
	}

	/*
	 * Handle incoming bluetooth message; server mode.
	 */
	private void processBTLine(String btLine) {
		String[] tokens = btLine.split("&");
		if (!tokens[0].startsWith("action=")) {
			throw new IllegalStateException("invalid btLine: " + btLine);
		}
		String action = tokens[0].substring(7);
		if (!tokens[1].startsWith("name=")) {
			throw new IllegalStateException("invalid btLine: " + btLine);
		}
		String name = tokens[1].substring(5);
		if (action.equals(ActionName.START)) {
			knowMeService.login(name);
		} else if (action.equals(ActionName.LINK_TO_SERVER_PLAYER)) {
			setOtherPlayerName(name);
			knowMeService.login(name);
			knowMeService.linkPlayers(name, playerName);
			String[] qa = knowMeService.getCurrentQuestion(name); // questions for client
			returnBTResults(ActionName.OTHER_PLAYER_NAME,
					new String[] { playerName });
			returnBTResults(ActionName.QUESTION, qa);
			this.questionArray = knowMeService.getCurrentQuestion(playerName); // questions for server
			poseQuestion(); // i.e. display question to this player (server)
		} else if (action.equals(ActionName.ANSWER)) {
			int myMe = Integer.parseInt(tokens[2].substring(5));
			int myHim = Integer.parseInt(tokens[3].substring(6));
			boolean bothAnswered = knowMeService.setMyAnswers(name, myMe, myHim);
			if (bothAnswered) {
				getAndDisplayResults();
			}
		} else if (action.equals(ActionName.NEXT_QUESTION)) {
			try {
				String[] questionArray = knowMeService.getNextQuestion(name);
				returnBTResults(ActionName.QUESTION, questionArray);
			} catch (EndOfQuestionsException e) {
				returnBTResults(ActionName.END_OF_QUESTIONS, null);
			}
		} else if (action.equals(ActionName.NEXT_QUIZ)) {
			try {
				String[] questionArray = knowMeService.getNextQuiz10(name);
				returnBTResults(ActionName.QUESTION, questionArray);
			} catch (EndOfQuestionsException e) {
				returnBTResults(ActionName.END_OF_QUIZZES, null);
			}
		} else {
			throw new IllegalStateException("unrecognised action: " + action);
		}
	}

	private void setOtherPlayerName(String otherPlayerName) {
		this.otherPlayerName = otherPlayerName;
		this.waitingStr = this.waitingTemplate.replace("{}", otherPlayerName);
		this.knowMeActivity.setOtherPlayerName(otherPlayerName);
	}

	public String getPlayerName() {
		return this.playerName;
	}
	/*
	 * Server mode only.
	 */
	private void getAndDisplayResults() {
		QAResults qaResults = knowMeService.getQAResults(playerName);

		// if player guesses wrongly, highlight his guess; otherwise don't
		if (qaResults.meRight)
			qaResults.hisHim = -1;
		if (qaResults.himRight)
			qaResults.hisMe = -1;

		displayResults(qaResults.summary, qaResults.myMe, qaResults.myHim,
				qaResults.hisHim, qaResults.hisMe, qaResults.percentCorrect);
		qaResults = knowMeService.getQAResults(otherPlayerName);
		if (qaResults.meRight)
			qaResults.hisHim = -1;
		if (qaResults.himRight)
			qaResults.hisMe = -1;
		String[] forwardResults = new String[2];
		forwardResults[0] = qaResults.summary;
		forwardResults[1] =
				"myMe=" + qaResults.myMe + "&myHim=" + qaResults.myHim + 
				"&hisHim=" + qaResults.hisHim + "&hisMe=" + qaResults.hisMe +
				"&percent=" + qaResults.percentCorrect;
		returnBTResults(ActionName.RESULTS, forwardResults);
	}

	private void displayResults(String resultSummary, int myMe, int myHim,
			int hisHim, int hisMe, int percentCorrect) {
		this.percentCorrect = percentCorrect;
		this.knowMeActivity.displayResults(this.questionArray,
				myMe, myHim, hisHim, hisMe);
	}

	public void handleDataReceived(String btMessage) {
		if (BuildConfig.DEBUG)
			Log.d(MainActivity.TAG, "handleDataReceived: " + btMessage);

		if (this.btServerMode) {
			String[] lines = btMessage.split("\n");
			for (String line : lines) {
				if (line != null && line.length() > 0) {
					processBTLine(line);
				}
			}
		} else { // i.e. client mode
			String[] lines = btMessage.split("\n");
			if (!lines[0].startsWith(ActionName.RESULT_TYPE)) {
				throw new RuntimeException("invalid BTMessage: " + btMessage);
			}
			String resultType = lines[0].substring(ActionName.RESULT_TYPE
					.length());
			if (resultType.equals(ActionName.RESULTS)) {
				// TODO: we could be a bit more rigorous here:
				// split each element of answers on "=", then
				// check first sub-element is correct, e.g. "myMe"
				String[] answers = lines[2].split("&");
				int myMe = Integer.parseInt(answers[0].substring(5));
				int myHim = Integer.parseInt(answers[1].substring(6));
				int hisHim = Integer.parseInt(answers[2].substring(7));
				int hisMe = Integer.parseInt(answers[3].substring(6));
				int percent = Integer.parseInt(answers[4].substring(8));
				displayResults(lines[1], myMe, myHim, hisHim, hisMe, percent);
			} else if (resultType.equals(ActionName.QUESTION)) {
				this.questionArray = new String[lines.length - 1];
				for (int i = 1; i < lines.length; i++) {
					this.questionArray[i - 1] = lines[i];
				}
				poseQuestion();
			} else if (resultType.equals(ActionName.END_OF_QUESTIONS)) {
				endOfQuestions();
			} else if (resultType.equals(ActionName.END_OF_QUIZZES)) {
				knowMeActivity.setStatusMessage("end of quizzes");
			} else if (resultType.equals(ActionName.OTHER_PLAYER_NAME)) {
				setOtherPlayerName(lines[1]);
			} else {
				Log.e(MainActivity.TAG, "unrecognised resultType: "
						+ resultType);
			}
		}
	}

	private void endOfQuestions() {
		knowMeActivity.endOfQuestions(this.percentCorrect);
	}

	private void returnBTResults(String resultType, String[] results) {
		if (this.singleDeviceMode) {
			if (BuildConfig.DEBUG)
				Log.d(MainActivity.TAG,
						"returnBTResults(resultType=" + resultType +
						"), but singleDeviceMode");
			return;
		}
		StringBuilder sb = new StringBuilder(ActionName.RESULT_TYPE
				+ resultType);
		if (results != null) {
			for (String result : results) {
				sb.append("\n" + result);
			}
		}
		bluetoothService.write(sb.toString().getBytes());
	}

	private void sendBTMessage(String action, String message) {
		if (singleDeviceMode) {
			if (BuildConfig.DEBUG)
				Log.d(MainActivity.TAG,
						"sendBTMessage(action=" + action +
						"), but singleDeviceMode");
			return;
		}
		String btMessage = "action=" + action + "&name=" + this.playerName;
		if (message != null)
			btMessage += message;
		btMessage += "\n";
		// Check that we're actually connected before trying anything
		if (bluetoothService.getState() == BluetoothService.BTState.connected) {
			bluetoothService.write(btMessage.getBytes());
		} else {
			Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_LONG)
					.show();
		}
	}

	public String connected(boolean serverMode) {
		this.btServerMode = serverMode;
		this.knowMeService = KnowMeService.getInstance();
		if (serverMode) {
			this.knowMeService.login(playerName);
		} else {
			sendBTMessage(ActionName.LINK_TO_SERVER_PLAYER, null);
		}
		return this.otherPlayerName;
	}

	public String onConnectionLost() {
		if (btServerMode) {
			knowMeService.logout(otherPlayerName);
		}
		return this.otherPlayerName;
	}

	public void setBluetoothService(BluetoothService bluetoothService) {
		this.bluetoothService = bluetoothService;
	}

	public void setSingleDeviceMode(String playerName, String otherPlayerName) {
		this.singleDeviceMode = true;
		this.playerName = playerName;
		setOtherPlayerName(otherPlayerName);
		connected(true);
		knowMeService.linkPlayers(otherPlayerName,
				this.playerName);
		this.questionArray = knowMeService.getCurrentQuestion(this.playerName);
		poseQuestion();
	}

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
