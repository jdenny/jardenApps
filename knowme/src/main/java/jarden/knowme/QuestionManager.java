package jarden.knowme;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Holds the master copy of all the questions, each of type MultiQA. Singleton.
 * @author john.denny@gmail.com
 * TODO: remove hisQuestion from file, and form it from symbols:
 	e.g. What {are you} most scared of? becomes:
 	myQuestion: What are you most scared of?
 	hisQuestion: What is {} most scared of?
		{you}		you			{}
		{are you}	are you		is {}
		{Are you}	Are you		Is {}
		{your}		your		{}'s
		{name}		{}			you
		{is name}	is {}		are you
		{Is name}	Is {}		Are you

 */
public class QuestionManager {
	private static QuestionManager instance = new QuestionManager();
	private String[] yesNoAnswers = {
			"Yes", "No"
	}; 
	
	public final static String QUESTIONS_DIRECTORY = "/home/john/knowme/";
	private final static String QUESTIONS_FILE = QUESTIONS_DIRECTORY +
			"questions.txt";
			// "sample.txt";
	private ArrayList<MultiQA> questionList;
	
	public static QuestionManager getInstance() {
		return instance;
	}
	private QuestionManager() {
	}
	/*
	 * Hangover from old days when it was run as free-standing app.
	 * The android app version calls loadQuestions(inputStream); see below.
	 * If we ever resurrect the web version of KnowMe, this class can be
	 * moved to JardenAppLib.
	 */
	public void loadQuestions() throws IOException {
		loadQuestions(new FileInputStream(QUESTIONS_FILE));
	}
	public void loadQuestions(InputStream inputStream) {
		BufferedReader reader = null;
		try {
			InputStreamReader isr = new InputStreamReader(inputStream);
			reader = new BufferedReader(isr);
			this.questionList = new ArrayList<MultiQA>();
			String line;
			String myQuestion = null;
			String hisQuestion = null;
			ArrayList<String> currentAnswers = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("Q: ")) {
					myQuestion = line.substring(3);
				} else if (line.startsWith("q: ")) {
					hisQuestion = line.substring(3);
				} else if (line.startsWith("A: ")) {
					currentAnswers.add(line.substring(3));
				} else if (line.startsWith("X:")) {
					questionList.add(new MultiQA(myQuestion, hisQuestion, currentAnswers));
					currentAnswers.clear();
				} else if (line.startsWith("W")) {
					myQuestion = line.substring(3);
					hisQuestion = "Who does {} think" +
							line.substring(6);
					questionList.add(new WhoMultiQA(myQuestion, hisQuestion));
				} else if (line.startsWith("Y")) {
					myQuestion = line.substring(3);
					line = reader.readLine();
					if (line != null && line.startsWith("y: ")) { 
						hisQuestion = line.substring(3);
						questionList.add(new MultiQA(myQuestion, hisQuestion, yesNoAnswers));
					} else {
						throw new IOException("expecting line 'y: ...' after 'Y: ...'");
					}
				}
			}
			reader.close();
			if (questionList.size() == 0) {
				throw new IOException("no questions found in inputStream");
			}
			Collections.shuffle(questionList);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	public MultiQA getQuestion(int index) throws EndOfQuestionsException {
		if (index >= questionList.size()) {
			throw new EndOfQuestionsException("no more questions of this type");
		}
		return questionList.get(index);
	}
	public int size() {
		return this.questionList.size();
	}

	public ArrayList<MultiQA> getQuestionList() {
		return this.questionList;
	}
}
