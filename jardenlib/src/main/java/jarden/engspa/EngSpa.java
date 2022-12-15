package jarden.engspa;

import jarden.provider.engspa.EngSpaContract.Topic;
import jarden.provider.engspa.EngSpaContract.Qualifier;
import jarden.provider.engspa.EngSpaContract.WordType;

/**
 * see jarden.android.provider.engspa.EngSpaContract
 * @author john.denny@gmail.com
 *
 */
public class EngSpa extends UserWord {
	private int id;
	private String english;
	private String spanish;
	private WordType wordType;
	private Qualifier qualifier;
	private Topic topic;
	private int level;

	public EngSpa(int id, String english, String spanish,
                  WordType wordType, Qualifier qualifier, Topic topic, int level) {
		super(-1, id);
		this.id = id;
		this.english = english;
		this.spanish = spanish;
		this.wordType = wordType;
		this.qualifier = qualifier;
		this.topic = topic; // also known as hint
		this.level = level;
	}
	@Override
	public String toString() {
		return english + ":" + spanish + "(" + super.toString() + ")";
	}
	@Override
	public boolean equals(Object other) {
		if (other instanceof EngSpa) {
			return ((EngSpa) other).id == this.id;
		} else return false;
	}
	@Override
	public int hashCode() {
		return id;
	}
	public String getDictionaryString() {
		return english + ": " + spanish + ", " + wordType + ", " + qualifier + ", " + topic;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public String getSpanish() {
		return spanish;
	}
	public void setSpanish(String spanish) {
		this.spanish = spanish;
	}
	public WordType getWordType() {
		return wordType;
	}
	public Qualifier getQualifier() {
		return qualifier;
	}
	public Topic getTopic() {
		return topic;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}

