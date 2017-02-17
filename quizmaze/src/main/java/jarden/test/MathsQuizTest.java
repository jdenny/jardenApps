package jarden.test;

import static org.junit.Assert.*;

import java.io.IOException;

import jarden.quiz.AlgebraQuiz;
import jarden.quiz.AmazeQuizCache;
import jarden.quiz.ArithmeticQuiz;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.Quiz;
import jarden.quiz.QuizCache;
import jarden.quiz.QuizCacheListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MathsQuizTest implements QuizCacheListener {
	private QuizCache quizCache;

	@Before
	public void setUp() throws Exception {
		quizCache = new AmazeQuizCache(this);
	}

	@After
	public void tearDown() throws Exception {
		quizCache = null;
	}
	
	@Test
	public void shouldGetArithmeticRight() throws IOException, EndOfQuestionsException {
		Quiz quiz = quizCache.getQuiz("Maths", "Arithmetic");
		assertTrue(quiz instanceof ArithmeticQuiz);
		for (int i = 0; i < 20; i++) {
			String question = quiz.getNextQuestion(1);
			String answerStr = quiz.getCorrectAnswer();
			int answer = Integer.parseInt(answerStr);
			String[] tokens = question.split(" ");
			assertEquals(4, tokens.length);
			int a = Integer.parseInt(tokens[0]);
			int b = Integer.parseInt(tokens[2]);
			int correctAnswer = 0;
			assertEquals(1, tokens[1].length());
			char op = tokens[1].charAt(0);
			switch (op) {
			case '+':
				correctAnswer = a + b;
				break;
			case '-':
				correctAnswer = a - b;
				break;
			case '*':
				correctAnswer = a * b;
				break;
			case '/':
				assertTrue(b != 0);
				correctAnswer = a / b;
				break;
			default:
				fail("invalid op: " + op);
			}
			assertEquals(correctAnswer, answer);
			assertEquals(Quiz.CORRECT, quiz.isCorrect(correctAnswer));
			assertEquals(Quiz.INCORRECT, quiz.isCorrect(correctAnswer + 1));
		}
	}
	@Test
	public void shouldGetAlgebraRight() throws IOException, EndOfQuestionsException {
		Quiz quiz = quizCache.getQuiz("Maths", "Algebra");
		assertTrue(quiz instanceof AlgebraQuiz);
		for (int i = 0; i < 20; i++) {
			String question = quiz.getNextQuestion(1);
			String answerStr = quiz.getCorrectAnswer();
			int answer = Integer.parseInt(answerStr);
			decodeQuestion(question, answer);
			assertEquals(Quiz.CORRECT, quiz.isCorrect(answer));
			assertEquals(Quiz.INCORRECT, quiz.isCorrect(answer + 1));
		}
	}
	private void decodeQuestion(String question, int answer) {
		String[] tokens = question.split(" ");
		int tokenIndex = 1;
		int a, b, c, d;
		// see if first token is '<a>x'
		String token = tokens[tokenIndex++];
		if (token.equals("x")) {
			a = 1;
			token = tokens[tokenIndex++];
		} else if (token.endsWith("x")) {
			a = Integer.parseInt(token.substring(0, token.length() - 1));
			token = tokens[tokenIndex++];
		} else {
			a = 0;
		}
		// next (or first) could be - or + or <b> or =
		boolean minus = false;
		if (token.equals("-")) {
			minus = true;
			token = tokens[tokenIndex++];
		} else if (token.equals("+")) {
			token = tokens[tokenIndex++];
		}
		if (token.equals("=")) {
			b = 0;
		} else {
			b = Integer.parseInt(token);
			token = tokens[tokenIndex++];
			if (minus) b = -b;
			assertTrue(token.equals("="));
		}
		token = tokens[tokenIndex++];
		
		// see if next token is '<c>x'
		if (token.equals("x")) {
			c = 1;
			if (tokenIndex < tokens.length) {
				token = tokens[tokenIndex++];
			} else token = null;
		} else if (token.endsWith("x")) {
			c = Integer.parseInt(token.substring(0, token.length() - 1));
			if (tokenIndex < tokens.length) {
				token = tokens[tokenIndex++];
			} else token = null;
		} else {
			c = 0;
		}
		// next could be - or + or <d> or nothing
		if (token != null) {
			minus = false;
			if (token.equals("-")) {
				minus = true;
				token = tokens[tokenIndex++];
			} else if (token.equals("+")) {
				token = tokens[tokenIndex++];
			}
			d = Integer.parseInt(token);
			if (minus) d = -d;
		} else {
			d = 0;
		}
		assertEquals(tokenIndex, tokens.length);
		// now check correct answer!
		assertEquals(a * answer + b, c * answer + d);
	}
	// test the test!
	@Test
	public void shouldHandleAllForms() {
		MathsQuizTest mqt = new MathsQuizTest();
		mqt.decodeQuestion("solve: x = 5", 5);
		// mqt.decodeQuestion("solve: x = 5", 6);
		mqt.decodeQuestion("solve: x + 1 = 5", 4);
		// mqt.decodeQuestion("solve: x + 1 = 5", 5);
		mqt.decodeQuestion("solve: 2x = x + 5", 5);
		mqt.decodeQuestion("solve: 3x - 2 = 2x", 2);
		mqt.decodeQuestion("solve: 2x - 2 = x + 5", 7);
		mqt.decodeQuestion("solve: 3x + 2 = 2x", -2);
		mqt.decodeQuestion("solve: 2x - 2 = x - 5", -3);
	}

	@Override
	public void onLogMessage(String message) {
		System.out.println(message);
	}
}
