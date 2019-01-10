package quiz;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Base class for all text-based question and answer quizzes.
 * @author John
 *
 */
public abstract class TextQAQuiz extends Quiz {
	private ArrayList<QuestionAnswer> qaList;
	private ArrayList<Integer> outstandingIndexList;
	private int index = -1;
	private String questionTemplate;
	
	public TextQAQuiz(ArrayList<QuestionAnswer> qaList) {
		this(qaList, "{0}?");
	}
	public TextQAQuiz(ArrayList<QuestionAnswer> qaList, String questionTemplate) {
		this.qaList = qaList;
		this.questionTemplate = questionTemplate;
		outstandingIndexList = new ArrayList<>();
		for (int i = 0; i < qaList.size(); i++) {
			outstandingIndexList.add(new Integer(i));
		}
		Collections.shuffle(outstandingIndexList);
	}
	@Override
	public String getNextQuestion() throws EndOfQuestionsException {
		if (outstandingIndexList.size() == 0) {
			throw new EndOfQuestionsException("No more questions");
		}
		index++;
		if (index >= outstandingIndexList.size()) {
			index = 0;
		}
		QuestionAnswer qa = qaList.get(outstandingIndexList.get(index));
		String question = MessageFormat.format(questionTemplate, qa.question);
		super.setQuestionAnswer(question, qa.answer);
		return question;
	}
	@Override
	public void notifyRightFirstTime() {
		outstandingIndexList.remove(index);
		index--; // otherwise we would miss out a question this time round
	}
}
