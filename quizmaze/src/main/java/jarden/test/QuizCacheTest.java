package jarden.test;

import java.io.IOException;

import jarden.quiz.AmazeQuizCache;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.PowersQuiz;
import jarden.quiz.PresetQuiz;
import jarden.quiz.Quiz;
import jarden.quiz.QuizCache;
import jarden.quiz.QuizCacheListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuizCacheTest implements QuizCacheListener {
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
	public void testAll() throws IOException, EndOfQuestionsException {
		Quiz quiz;
		/* this was in the old days of my Python running on Peet's server!
		quiz = quizCache.getQuiz("Spanish", "animal.spa");
		if (!(quiz instanceof PresetQuiz)) {
			System.out.println("expecting PresetQuiz; obtained: " + quiz);
		} else {
			System.out.println(quiz.getNextQuestion(1) + "; "
					+ quiz.getAnswer());
		}
		*/
		quiz = quizCache.getQuiz("Spanish", "inglaterra.properties");
		if (!(quiz instanceof PresetQuiz)) {
			System.out.println("expecting PresetQuiz; obtained: " + quiz);
		} else {
			System.out.println(quiz.getNextQuestion(1) + "; "
					+ quiz.getCorrectAnswer());
		}
		quiz = quizCache.getQuiz("Spanish", "hora.txt");
		if (!(quiz instanceof PresetQuiz)) {
			System.out.println("expecting PresetQuiz; obtained: " + quiz);
		} else {
			System.out.println(quiz.getNextQuestion(1) + "; "
					+ quiz.getCorrectAnswer());
		}
		quiz = quizCache.getQuiz("Maths", "Powers");
		if (!(quiz instanceof PowersQuiz)) {
			System.out.println("expecting PowersQuiz; obtained: " + quiz);
		} else {
			System.out.println(quiz.getNextQuestion(1) + "; "
					+ quiz.getCorrectAnswer());
		}
		System.out.println("adios John");

	}

	@Override
	public void onLogMessage(String message) {
		System.out.println(message);
	}

	/*
	@Test
	public void testGetLocalFileNames() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFileInputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFileOutputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testLogCacheMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testAmazeQuizCache() {
		fail("Not yet implemented");
	}

	@Test
	public void testQuizCache() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFileSubtypeNames() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSpanishSubtypeNames() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetQuiz() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMathsSubtype() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadQuizFromLocalFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testCopyQuizToLocalDisk() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadQuizFromServer() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetHighScore() {
		fail("Not yet implemented");
	}
	*/

}
