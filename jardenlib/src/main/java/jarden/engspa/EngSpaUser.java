package jarden.engspa;

import android.content.SharedPreferences;

import jarden.engspa.EngSpaQuiz.QuizMode;
import jarden.provider.engspa.EngSpaContract.QAStyle;

public class EngSpaUser {
	private static final String LEARN_LEVEL_KEY = "LEARN_LEVEL";
	private static final String LEARN_MODE_PHASE2_KEY = "LEARN_MODE_PHASE2";
	private static final String QA_STYLE_KEY = "QA_STYLE";
	private static final String QUIZ_MODE_KEY = "QUIZ_MODE";
	private static final String RACE_LEVEL_KEY = "RACE_LEVEL";

	private SharedPreferences sharedPreferences;
	private int userId;
	private String userName;
	private int userLevel = -1;
	private int raceLevel = -1;
	private QAStyle qaStyle = null;
	private QuizMode quizMode = null;
	private Boolean learnModePhase2 = null;

	public EngSpaUser(int userId, String userName, int userLevel,
			QAStyle qaStyle) {
		this(userName, userLevel, qaStyle);
		this.userId = userId;
	}
	public EngSpaUser(String userName, int userLevel,
			QAStyle qaStyle) {
		this.userName = userName;
		this.userLevel = userLevel;
		this.qaStyle = qaStyle;
	}
	public void setSharedPreferences(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}
	@Override
	public String toString() {
		return "EngSpaUser [userLevel=" + userLevel +
				", learnModePhase2=" + learnModePhase2 +
				", raceLevel=" + raceLevel +
				", qaStyle=" + qaStyle +
				", quizMode=" + quizMode + "]";
	}
	public int getUserId() {
		return userId;
	}
	public String getUserName() {
		return userName;
	}
	public int getUserLevel() {
		return userLevel;
	}
	public QAStyle getQAStyle() {
		return qaStyle;
	}
	public void setQAStyle(QAStyle qaStyle) {
		this.qaStyle = qaStyle;
	}
	public void setId(int userId) {
		this.userId = userId;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
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
}
