package com.jardenconsulting.androidcourse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jarden.provider.engspa.EngSpaContract;
import jarden.quiz.ArithmeticQuiz;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.PresetQuiz;
import jarden.quiz.QuestionAnswer;
import jarden.quiz.Quiz;
import jarden.quiz.QuizListener;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity implements OnClickListener,
		OnItemSelectedListener, QuizListener {
	public final static String TAG = "MainActivity";
	public final static String[] quizTypes = {
		"Arithmetic",
		"Capitals",
		"Spanish",
		"SpanishDB"
	};
	public final static String serverUrlStr =
		"https://sites.google.com/site/amazequiz/home/problems/";

	private TextView questionTextView;
	private TextView levelTextView;
	private EditText answerEditText;
	private TextView resultTextView;
	private Quiz quiz;
	private ArithmeticQuiz arithmeticQuiz;
	private PresetQuiz capitalsQuiz; // from raw (local) file
	private PresetQuiz spanishQuiz; // from network
	private PresetQuiz engSpaQuiz; // from database
	
	private Spinner quizTypeSpinner;
	private ArrayAdapter<String> quizTypeAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		this.levelTextView = (TextView) findViewById(R.id.level);
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		int defaultValue = getResources().getInteger(R.integer.defaultLevel);
		int savedLevel = sharedPref.getInt(getString(R.string.savedLevelTag), defaultValue);
		this.levelTextView.setText(String.valueOf(savedLevel));
		this.questionTextView = (TextView) findViewById(R.id.question);
		this.answerEditText = (EditText) findViewById(R.id.answer);
		Button button = (Button) findViewById(R.id.buttonGo);
		button.setOnClickListener(this);
		this.resultTextView = (TextView) findViewById(R.id.result);
		quizTypeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				quizTypes);
		quizTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		quizTypeSpinner = (Spinner) findViewById(R.id.quizTypeSpinner);
		quizTypeSpinner.setAdapter(quizTypeAdapter);
		quizTypeSpinner.setOnItemSelectedListener(this);
		loadQuizzes();
	}
	private void loadQuizzes() {
		// do this in a background thread, as cannot access the network on main thread
		Runnable runnable = new Runnable() {
            private String message = "end of loadQuizzes()";
			@Override
			public void run() {
				InputStream inputStream = getResources().openRawResource(R.raw.capitals);
				Properties capitalProps = new Properties();
				try {
					capitalProps.load(inputStream);
					capitalsQuiz = new PresetQuiz(capitalProps);
				} catch (IOException e) {
                    message = "exception trying to create capitalsQuiz: " + e;
					Log.e(TAG, message);
				}
				try {
					URL spanishURL = new URL(serverUrlStr + "spanish.txt?attredirects=0&d=1");
					// iso-8859 needed for Android, and maybe for Java;
					spanishQuiz = new PresetQuiz(spanishURL.openStream(), "iso-8859-1");
				} catch (IOException e) {
                    message = "exception trying to create spanishQuiz: " + e;
					Log.e(TAG, message);
				}
				ContentResolver contentResolver = getContentResolver();
				List<QuestionAnswer> engSpaList = new ArrayList<QuestionAnswer>();
				String selection = null;
				String sortOrder = null;
				String[] projection = { EngSpaContract.ENGLISH, EngSpaContract.SPANISH };
				Cursor cursor = contentResolver.query(
						EngSpaContract.CONTENT_URI_ENGSPA,
						projection,
						selection,
						null, sortOrder);
				if (cursor == null) {
                    message = "no matching DB entries found!";
                    Log.w(TAG, message);
				} else {
					while (cursor.moveToNext()) {
						String english = cursor.getString(0);
						String spanish = cursor.getString(1);
						engSpaList.add(new QuestionAnswer(english, spanish));
					}
					cursor.close();
				}
				engSpaQuiz = new PresetQuiz(engSpaList);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(QuizActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
			}
		};
		new Thread(runnable).start();
		this.arithmeticQuiz = new ArithmeticQuiz();
		setQuizType(arithmeticQuiz);
	}
	private void setQuizType(Quiz quiz) {
		this.quiz = quiz;
		this.quiz.setQuizListener(this);
		int answerType = this.quiz.getAnswerType(); 
		if (answerType == Quiz.ANSWER_TYPE_INT) {
			answerEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (answerType == Quiz.ANSWER_TYPE_DOUBLE) {
			answerEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		} else {
			answerEditText.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		this.quiz.reset();
		showNextQuestion();
	}

	private void showNextQuestion() {
		this.answerEditText.getText().clear();
		int level =
				Integer.parseInt(this.levelTextView.getText().toString());
		try {
			String question = this.quiz.getNextQuestion(level);
			questionTextView.setText(question);
		} catch (EndOfQuestionsException e) {
			Log.e(TAG, "exception in showNextQuestion(): " + e);
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.buttonGo) {
			String answerStr = this.answerEditText.getText().toString();
			Log.d(TAG, "Go button pressed; answer is " + answerStr);
			String resultStr;
            int res = this.quiz.isCorrect(answerStr);
            if (res == Quiz.CORRECT) {
                resultStr = getString(R.string.correctStr);
                showNextQuestion();
            } else if (res == Quiz.FAIL) {
                resultStr = getString(R.string.wrongStr) + " The correct answer is: " +
                        this.quiz.getCorrectAnswer();
                showNextQuestion();
            } else {
                resultStr = getString(R.string.wrongStr) + " Try again";
            }
			this.resultTextView.setText(resultStr);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_quiz, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {
		if (id == 0) setQuizType(arithmeticQuiz);
		else if (id == 1) {
			if (this.capitalsQuiz == null) {
				Toast.makeText(this, "capitalsQuiz not available!",
						Toast.LENGTH_LONG).show();
			} else {
				setQuizType(capitalsQuiz);
			}
		}
		else if (id == 2) {
			if (this.spanishQuiz == null) {
				Toast.makeText(this, "spanishQuiz not available!",
						Toast.LENGTH_LONG).show();
			} else {
				setQuizType(spanishQuiz);
			}
		}
		else if (id == 3) {
			if (this.engSpaQuiz == null) {
				Toast.makeText(this, "engSpaQuiz not available!",
						Toast.LENGTH_LONG).show();
			} else {
				setQuizType(engSpaQuiz);
			}
		}
		else {
			Log.w(TAG,
				"QuizActivity.onItemSelected(); unexpected id=" + id);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.w(TAG, "QuizActivity.onNothingSelected()");
	}
	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		int defaultValue = getResources().getInteger(R.integer.defaultLevel);
		int savedLevel = sharedPref.getInt(getString(R.string.savedLevelTag), defaultValue);
		int currentLevel = Integer.parseInt(this.levelTextView.getText().toString());
		if (currentLevel > savedLevel) {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(getString(R.string.savedLevelTag), currentLevel);
			editor.commit();
		}
	}
	// QuizListener methods:
	@Override
	public void onRightAnswer() {
		// nothing to do
	}
	@Override
	public void onWrongAnswer() {
		// nothing to do
	}
	@Override
	public void onThreeRightFirstTime() {
		int currentLevel = Integer.parseInt(this.levelTextView.getText().toString());
		this.levelTextView.setText(String.valueOf(currentLevel + 1));
	}
	@Override
	public void onReset() {
		// nothing to do
	}
	@Override
	public void onEndOfQuestions() {
		this.resultTextView.setText("end of questions reached");
	}

}
