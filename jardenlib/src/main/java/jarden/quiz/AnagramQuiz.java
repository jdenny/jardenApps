package jarden.quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Read words from a file or database, and create anagram from each one.
 * @author john.denny@gmail.com
 */
public class AnagramQuiz extends Quiz {
	private ArrayList<String> masterWordList;
	private ArrayList<Integer> outstandingIndexList;
	private int index = -1;
	private int currentLevel = 0; // word size is level + 2, i.e. starts at 3
	private String questionTemplate = null;

	public AnagramQuiz(InputStream is, String encoding) throws IOException {
		this(new InputStreamReader(is, encoding));
	}
	public AnagramQuiz(InputStreamReader isReader) throws IOException {
		BufferedReader reader = new BufferedReader(isReader);
		masterWordList = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			if (!line.startsWith("#")) { // comment
				masterWordList.add(line);
			}
		}
		reader.close();
	}
	public AnagramQuiz(ArrayList<String> words) {
		this.masterWordList = words;
	}
	private String randomise(String word) {
		StringBuilder builder = new StringBuilder(word);
		int size = word.length();
		int index;
		for (int i = 0; i < size; i++) {
			index = randomNum.nextInt(size);
			char ch = builder.charAt(i);
			builder.setCharAt(i, builder.charAt(index));
			builder.setCharAt(index, ch);
		}
		return builder.toString();
	}
	public String getQuestionTemplate() {
		return questionTemplate;
	}
	@Override
	public void reset() {
		super.reset();
	}
	@Override
	public String getNextQuestion(int level) throws EndOfQuestionsException {
		if (level != this.currentLevel) {
			this.outstandingIndexList = new ArrayList<Integer>();
			this.currentLevel = level;
			int wordSize = level + 2;
			int i;
			for (i = 0; i < this.masterWordList.size() &&
					masterWordList.get(i).length() < wordSize; i++);
			for (; i < this.masterWordList.size() &&
					masterWordList.get(i).length() == wordSize; i++) {
				outstandingIndexList.add(i);
			}
			Collections.shuffle(outstandingIndexList);
			this.index = -1;
		}
		if (outstandingIndexList.size() == 0) {
			throw new EndOfQuestionsException("No more questions");
		}
		this.index++;
		if (index >= outstandingIndexList.size()) {
			index = 0;
		}
		String answer = this.masterWordList.get(outstandingIndexList.get(index));
		String question = randomise(answer);
		super.setQuestionAnswer(question, answer);
		return question;
	}
	@Override
	public void notifyRightFirstTime() {
		outstandingIndexList.remove(index);
		index--; // otherwise we would miss out a question this time round
	}
	@Override
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_STRING;
	}
	@Override
	public String getHint() {
		int len = getAttempts();
		String answer = super.getCorrectAnswer();
		if (len > answer.length()) {
			len = answer.length();
		}
		return answer.substring(0, len);
	}
	public static int engSpa2Word(InputStream is, OutputStream os) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		ArrayList<String> wordList = new ArrayList<String>();
		String line;
		String word;
		while ((line = reader.readLine()) != null) {
			if (line.length() > 0) {
				int firstCommaIndex = line.indexOf(',');
				word = line.substring(0, firstCommaIndex);
				if (word.length() > 2) {
					wordList.add(word);
				}
			}
		}
		reader.close();
		// now sort by length
		Collections.sort(wordList, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.length() - rhs.length();
			}
		});
		PrintWriter writer = new PrintWriter(os);
		for (String word2: wordList) {
			writer.println(word2);
		}
		writer.close();
		return wordList.size();
	}
}
