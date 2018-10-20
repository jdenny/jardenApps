package quiz;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;

import jarden.quiz.FractionsQuiz;
import jarden.quiz.PowersQuiz;
import jarden.quiz.Quiz;
import jarden.quiz.SeriesQuiz;
import jarden.quiz.TimesQuiz;

public class SimpleQuizClient {
	private final static String WORD_FILE_NAME = "docs/words.txt";
	private final static String CAPITALS_FILE_NAME = "docs/capitals.properties";
	private static Scanner scanner = new Scanner(System.in);
	private static Quiz quiz;
	
	public static void main(String[] args) {
		while (true) {
			System.out.println(
					"Quit or quiz type: Anagrams, Capitals, aLgebra, aReas, Powers, "
					+ "Fractions, Series, Times or default to arithmetic");
			String mathsType = scanner.nextLine();
			if (mathsType.equalsIgnoreCase("quit") || mathsType.equalsIgnoreCase("q")) {
				System.out.println("and it's goodnight from me");
				scanner.close();
				return;
			} else if (mathsType.equalsIgnoreCase("algebra") || mathsType.equalsIgnoreCase("l")) {
				quiz = new AlgebraQuiz();
			} else if (mathsType.equalsIgnoreCase("areas") || mathsType.equalsIgnoreCase("r")) {
					quiz = new AreasQuiz();
			} else if (mathsType.equalsIgnoreCase("powers") || mathsType.equalsIgnoreCase("p")) {
				quiz = new PowersQuiz();
			} else if (mathsType.equalsIgnoreCase("fractions") || mathsType.equalsIgnoreCase("f")) {
				quiz = new FractionsQuiz();
			} else if (mathsType.equalsIgnoreCase("series") || mathsType.equalsIgnoreCase("s")) {
				quiz = new SeriesQuiz();
			} else if (mathsType.equalsIgnoreCase("times") || mathsType.equalsIgnoreCase("t")) {
				quiz = new TimesQuiz();
			} else if (mathsType.equalsIgnoreCase("anagrams") || mathsType.equalsIgnoreCase("a")) {
				FileInputStream fis;
				try {
					fis = new FileInputStream(WORD_FILE_NAME);
					quiz = new AnagramQuiz(new InputStreamReader(fis));
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			} else if (mathsType.equalsIgnoreCase("capitals") || mathsType.equalsIgnoreCase("c")){
				FileInputStream fis;
				try {
					fis = new FileInputStream(CAPITALS_FILE_NAME);
					Properties properties = new Properties();
					properties.load(fis);
					fis.close();
					quiz = new PresetQuiz(properties);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			} else {
				quiz = new ArithmeticQuiz();
			}
			runQuiz(quiz);
		}
	}
	private static void runQuiz(Quiz quiz) {
		int level = 1;
		int correctAnswers = 0;
		System.out.println("supply blank answer to exit from this quiz type");
		try {
			String question = quiz.getNextQuestion(level);
	
			while (true) {
				System.out.println(question);
				String answer = scanner.nextLine();
				if (answer.length() == 0) return;
				int result = quiz.isCorrect(answer);
				if (result == Quiz.CORRECT) {
					System.out.println("correct");
					++correctAnswers;
					if (correctAnswers >= 3) {
						++level;
						correctAnswers = 0;
						System.out.println("level: " + level);
					}
					question = quiz.getNextQuestion(level);
				} else if (result == Quiz.INCORRECT) {
					StringBuilder sb = new StringBuilder("Wrong!");
					String hint = quiz.getHint();
					if (hint != null) {
						sb.append(" Hint: " + hint);
					}
					System.out.println(sb);
				} else { // FAIL
					System.out.println("Wrong! The correct answer is: " + quiz.getCorrectAnswer());
					question = quiz.getNextQuestion(level);
				}
			}
		} catch(EndOfQuestionsException e) {
			System.out.println("end of questions");
		}
	}

}
