package jarden.quiz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Keeps a track of all the questions incorrectly answered in this session.
 * @author John
 */
public class FailuresQuiz extends PresetQuiz {
	private List<QuestionAnswer> qaList;
	private HashSet<QuestionAnswer> qaSet;
	
	public FailuresQuiz() {
		super(new ArrayList<QuestionAnswer>());
		qaList = super.getQuestionAnswerList();
		qaSet = new HashSet<QuestionAnswer>();
	}
	public void add(QuestionAnswer qa) {
		if (!qaSet.contains(qa)) {
			qaList.add(qa);
			qaSet.add(qa);
		}
	}
	

}
