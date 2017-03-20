package jarden.knowme;

import java.util.ArrayList;

public class MultiQA {
	public static final int MAX_ANSWER_COUNT = 4;

	private String myQuestion;
	private String hisQuestion;
	private String[] answers;
	
	public MultiQA(String myQuestion, String hisQuestion, ArrayList<String> answers) {
		this(myQuestion, hisQuestion, answers.toArray(new String[0]));
	}
	public MultiQA(String myQuestion, String hisQuestion, String[] answers) {
		if (answers.length > MAX_ANSWER_COUNT) {
			throw new IllegalArgumentException("too many answers for question: " +
					myQuestion);
		}
		this.myQuestion = myQuestion;
		this.hisQuestion = hisQuestion;
		this.answers = answers;
	}
	/** TODO:
	replace symbols:
		myQuestion.replace("{you}", "you");
		hisQuestion.replace("{you}", hisName);
		Q: What {are you} most scared of?
			{you} -> Jack
			{are you} -> is Jack
			{Are you} -> Is Jack
			{your} -> Jack's
			{name} -> you
			{is name} -> are you
			{Is name} -> Are you
	 */
	public String getMyQuestion(Player player) {
		return this.myQuestion.replace("{}", player.getOtherPlayer().getName());
	}
	public String getHisQuestion(Player player) {
		return this.hisQuestion.replace("{}", player.getOtherPlayer().getName());
	}
	public String[] getAnswers(Player player) {
		return this.answers;
	}
}

