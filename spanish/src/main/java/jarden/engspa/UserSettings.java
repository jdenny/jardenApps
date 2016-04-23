package jarden.engspa;

import android.content.SharedPreferences;

import jarden.engspa.EngSpaQuiz.QuizMode;
import jarden.provider.engspa.EngSpaContract.QAStyle;

/**
 * Created by john.denny@gmail.com on 22/04/2016.
 */
public class UserSettings {
	private static final String LEARN_LEVEL_KEY = "LEARN_LEVEL";
	private static final String LEARN_MODE_PHASE2_KEY = "LEARN_MODE_PHASE2";
	private static final String QA_STYLE_KEY = "QA_STYLE";
	private static final String QUIZ_MODE_KEY = "QUIZ_MODE";
	private static final String RACE_LEVEL_KEY = "RACE_LEVEL";

	private SharedPreferences sharedPreferences;
	private int learnLevel = -1;
	private int raceLevel = -1;
	private QAStyle qaStyle = null;
	private QuizMode quizMode = null;
	private Boolean learnModePhase2 = null;

	public UserSettings(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}
	@Override
	public String toString() {
		return "UserSettings{" +
				"learnLevel=" + learnLevel +
				", raceLevel=" + raceLevel +
				", qaStyle=" + qaStyle +
				", quizMode=" + quizMode +
				'}';
	}
	public int getLearnLevel() {
		if (this.learnLevel < 1) {
			this.learnLevel = sharedPreferences.getInt(LEARN_LEVEL_KEY, 1);
		}
		return this.learnLevel;
	}
	public void setLearnLevel(int learnLevel) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(LEARN_LEVEL_KEY, learnLevel);
		editor.apply();
		this.learnLevel = learnLevel;
	}
	public int getRaceLevel() {
		if (this.raceLevel < 1) {
			this.raceLevel = sharedPreferences.getInt(RACE_LEVEL_KEY, 1);
		}
		return this.raceLevel;
	}
	public void setRaceLevel(int raceLevel) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(RACE_LEVEL_KEY, raceLevel);
		editor.apply();
		this.raceLevel = raceLevel;
	}
	public boolean getLearnModePhase2() {
		if (this.learnModePhase2 == null) {
			this.learnModePhase2 = sharedPreferences.getBoolean(LEARN_MODE_PHASE2_KEY, false);
		}
		return this.learnModePhase2;
	}
	public void setLearnModePhase2(boolean learnModePhase2) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(LEARN_LEVEL_KEY, learnModePhase2);
		editor.apply();
		this.learnModePhase2 = learnModePhase2;
	}
	public QAStyle getQAStyle() {
		String qaStyleStr = sharedPreferences.getString(QA_STYLE_KEY, null);
		return (qaStyleStr == null) ?
				QAStyle.spokenWrittenSpaToEng : QAStyle.valueOf(qaStyleStr);
	}
	public void setQAStyle(QAStyle qaStyle) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(LEARN_LEVEL_KEY, qaStyle.name());
		editor.apply();
	}
	public QuizMode getQuizMode() {
		String quizModeStr = sharedPreferences.getString(QUIZ_MODE_KEY, null);
		return (quizModeStr == null) ?
				QuizMode.LEARN : QuizMode.valueOf(quizModeStr);
	}
	public void setQuizMode(QuizMode quizMode) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(QUIZ_MODE_KEY, quizMode.name());
		editor.apply();
	}
}

