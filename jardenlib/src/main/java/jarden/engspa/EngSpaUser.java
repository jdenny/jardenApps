package jarden.engspa;

import android.content.SharedPreferences;

import jarden.engspa.EngSpaQuiz.QuizMode;
import jarden.provider.engspa.EngSpaContract;
import jarden.provider.engspa.EngSpaContract.QAStyle;

public class EngSpaUser {
	private static final String LEARN_LEVEL_KEY = "LEARN_LEVEL";
	private static final String LEARN_MODE_PHASE2_KEY = "LEARN_MODE_PHASE2";
	private static final String QA_STYLE_KEY = "QA_STYLE";
	private static final String QUIZ_MODE_KEY = "QUIZ_MODE";
	private static final String RACE_LEVEL_KEY = "RACE_LEVEL";
    private static final String TOPIC_KEY = "TOPIC";

	private SharedPreferences sharedPreferences;
	// cached copies of values held in sharedPreferences:
    private int learnLevel = -1;
	private int raceLevel = -1;
	private QAStyle qaStyle = null;
	private QuizMode quizMode = null;
	private Boolean learnModePhase2 = null;
    private String topic = null;
    // end of cached copies

	public EngSpaUser(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}
	@Override
	public String toString() {
		return "EngSpaUser [learnLevel=" + learnLevel +
				", learnModePhase2=" + learnModePhase2 +
				", raceLevel=" + raceLevel +
				", qaStyle=" + qaStyle +
				", quizMode=" + quizMode +
                ", topic=" + topic + "]";
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
        if (isLearnModePhase2()) setLearnModePhase2(false);
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
	public boolean isLearnModePhase2() {
		if (this.learnModePhase2 == null) {
			this.learnModePhase2 = sharedPreferences.getBoolean(
                    LEARN_MODE_PHASE2_KEY, false);
		}
		return this.learnModePhase2;
	}
	public void setLearnModePhase2(boolean learnModePhase2) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(LEARN_MODE_PHASE2_KEY, learnModePhase2);
		editor.apply();
		this.learnModePhase2 = learnModePhase2;
	}
	public QAStyle getQAStyle() {
        if (this.qaStyle == null) {
            String qaStyleStr = sharedPreferences.getString(QA_STYLE_KEY, null);
            this.qaStyle = (qaStyleStr == null) ?
                    QAStyle.spokenWrittenSpaToEng : QAStyle.valueOf(qaStyleStr);
        }
        return this.qaStyle;
	}
	public void setQAStyle(QAStyle qaStyle) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(QA_STYLE_KEY, qaStyle.name());
		editor.apply();
        this.qaStyle = qaStyle;
	}
	public QuizMode getQuizMode() {
        if (this.quizMode == null) {
            String quizModeStr = sharedPreferences.getString(QUIZ_MODE_KEY, null);
            this.quizMode = (quizModeStr == null) ?
                    QuizMode.LEARN : QuizMode.valueOf(quizModeStr);
        }
        return this.quizMode;
	}
	public void setQuizMode(QuizMode quizMode) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(QUIZ_MODE_KEY, quizMode.name());
		editor.apply();
        this.quizMode = quizMode;
	}
    public String getTopic() {
        if (this.topic == null) {
            this.topic = sharedPreferences.getString(TOPIC_KEY,
                    EngSpaContract.Attribute.colour.toString());
        }
        return topic;
    }
    public void setTopic(String topic) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOPIC_KEY, topic);
        editor.apply();
        this.topic = topic;
    }
}
