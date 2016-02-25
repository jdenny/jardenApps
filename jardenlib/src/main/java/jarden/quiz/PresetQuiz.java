package jarden.quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Text-based question and answer quizzes.
 * Questions and answers can come from various sources, hence different constructors.
 * @author john.denny@gmail.com
 */
public class PresetQuiz extends Quiz {
	private List<QuestionAnswer> qaList;
	private List<Integer> outstandingIndexList;
	private int index = -1;
	private String questionTemplate = null;

	public PresetQuiz(InputStream is, String encoding) throws IOException {
		this(new InputStreamReader(is, encoding));
	}
	public PresetQuiz(InputStreamReader isReader) throws IOException {
		BufferedReader reader = new BufferedReader(isReader);
		qaList = new ArrayList<QuestionAnswer>();
		String question = null;
		String answer;
		while (true) {
			String line = reader.readLine();
			if (line == null) break; // end of file
			if (line.startsWith("#")) continue;
			if (line.startsWith("Q: ")) {
				question = line.substring(3);
			} else if (line.startsWith("A: ")) {
				// if it's an old-fashioned multi-choice quiz, convert it to
				// simple QA, using only first answer:
				if (question != null) {
					answer = line.substring(3);
					qaList.add(new QuestionAnswer(question, answer));
					question = null;
				}
			} else if (line.startsWith("$T: ")) {
				questionTemplate = line.substring(4);
			} else if (line.startsWith("$IO: ")) {
				char questionStyle = line.charAt(5);
				char answerStyle = line.charAt(6);
				setQuestionStyle(questionStyle);
				setAnswerStyle(answerStyle);
			} else if (line.startsWith("QA: ")) {
				answer = line.substring(4);
				qaList.add(new QuestionAnswer(answer, answer));
			} else {
				System.out.println("unrecognised line: " + line);
			}
		}
		reader.close();
		common();
	}
	private void common() {
		/*! if okay, replace calls to common() with reset()
		if (questionTemplate == null) questionTemplate = "";
		else if (!questionTemplate.endsWith(" ")) {
			questionTemplate += " ";
		}
		*/
		reset();
	}
	/**
	 * Build a Quiz from properties, where for each property,
	 * name is the question, and value is the answer.
	 */
	public PresetQuiz(Properties properties) throws IOException {
		Set<String> names = properties.stringPropertyNames();
		qaList = new ArrayList<QuestionAnswer>();
		for (String name: names) {
			String value = properties.getProperty(name);
			if (name.equals(Quiz.TEMPLATE_KEY)) {
				questionTemplate = value;
			} else if (name.equals(Quiz.IO_KEY)) {
				setQuestionStyle(value.charAt(0));
				setAnswerStyle(value.charAt(1));
			} else {
				qaList.add(new QuestionAnswer(name, value));
			}
		}
		common();
	}
	/**
	 * Build a Quiz from a List of QuestionAnswer objects.
	 * QuestionAnswer has a constructor:
	 *    public QuestionAnswer(String question, String answer)
	 */
	public PresetQuiz(List<QuestionAnswer> qaList) {
		this(qaList, null);
	}
	/**
	 * Build a Quiz from a List of QuestionAnswer objects.
	 * QuestionAnswer has a constructor:
	 *    public QuestionAnswer(String question, String answer)
	 *    
	 * @param questionTemplate: String containing "{}" which is
	 * 		used in getNextQuestion()
	 * E.g. template is "what is the capital of {}?"
	 * 		question is "France"
	 * 		getNextQuestion() returns "what is the capital of France?"
	 */
	public PresetQuiz(List<QuestionAnswer> qaList, String questionTemplate) {
		this.qaList = qaList;
		this.questionTemplate = questionTemplate;
		common();
	}
	public List<QuestionAnswer> getQuestionAnswerList() {
		return qaList;
	}
	public String getQuestionTemplate() {
		return questionTemplate;
	}
	@Override
	public void reset() {
		super.reset();
		outstandingIndexList = new ArrayList<Integer>();
		for (int i = 0; i < qaList.size(); i++) {
			outstandingIndexList.add(Integer.valueOf(i));
		}
		Collections.shuffle(outstandingIndexList);
	}
	@Override
	public String getNextQuestion(int level) throws EndOfQuestionsException {
		if (outstandingIndexList.size() == 0) {
			throw new EndOfQuestionsException("No more questions");
		}
		index++;
		if (index >= outstandingIndexList.size()) {
			index = 0;
		}
		QuestionAnswer qa = qaList.get(outstandingIndexList.get(index));
		String question;
		if (questionTemplate == null) {
			question = qa.question;
		} else {
			// I don't know how this ever worked, but it assumes the template contains {0}
			// whereas Capitals.properties, for example, contains {}
			// question = MessageFormat.format(questionTemplate, qa.question);
			question = questionTemplate.replace("{}", qa.question);
		}
		super.setQuestionAnswer(question, qa.answer);
		return question;
	}
	@Override
	public void notifyRightFirstTime() {
		if (index >= 0) {
			outstandingIndexList.remove(index);
			index--; // otherwise we would miss out a question this time round
		}
	}
	@Override
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_STRING;
	}
	@Override
	public String getHint() {
		int len = getAttempts() * 2;
		String answer = super.getCorrectAnswer();
		if (len > answer.length()) {
			len = answer.length();
		}
		return answer.substring(0, len);
	}
}
