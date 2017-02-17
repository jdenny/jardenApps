package jarden.test;

import static org.junit.Assert.*;

import java.io.IOException;

import jarden.quiz.AmazeQuizCache;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.Quiz;
import jarden.quiz.QuizCache;
import jarden.quiz.QuizCacheListener;
import jarden.quiz.QuizListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuizCallbackTest implements QuizCacheListener {
	private QuizCache quizCache;

	@Before
	public void setUp() throws Exception {
		quizCache = new AmazeQuizCache(this);
	}

	@After
	public void tearDown() throws Exception {
		quizCache = null;
	}
	
	class MyQuizListener implements QuizListener {
		int onWrongAnswerCt = 0;
		int onRightAnswerCt = 0;
		int onThreeRightFirstTimeCt = 0;
		int onResetCt = 0;
		
		@Override
		public void onRightAnswer() {
			++onRightAnswerCt;
		}
		@Override
		public void onWrongAnswer() {
			++onWrongAnswerCt;
		}
		@Override
		public void onThreeRightFirstTime() {
			++onThreeRightFirstTimeCt;
		}
		@Override
		public void onReset() {
			++onResetCt;
		}
		@Override
		public void onEndOfQuestions() {
			fail("shouldn't have reached onEndOfQuestions");
			
		}
	}

	@Test
	public final void testQuizCallbacks() throws IOException, EndOfQuestionsException {
		Quiz quiz = quizCache.getQuiz("Maths", "Arithmetic");
		MyQuizListener mqListener = new MyQuizListener();
		quiz.setQuizListener(mqListener);
		quiz.getNextQuestion(1);
		String answerStr = quiz.getCorrectAnswer();
		int answer = Integer.parseInt(answerStr);
		++answer;
		int result;
		// give wrong answer 3 times:
		for (int i = 1; i <= 3; i++) {
			result = quiz.isCorrect(answer);
			if (i < 3) {
				assertEquals(Quiz.INCORRECT, result);
			} else {
				assertEquals(Quiz.FAIL, result);
			}
			assertEquals(i, mqListener.onWrongAnswerCt);
			assertEquals(0, mqListener.onRightAnswerCt);
			assertEquals(0, mqListener.onThreeRightFirstTimeCt);
		}
		mqListener.onWrongAnswerCt = 0;
		// give right answer 3 times:
		for (int i = 1; i <= 3; i++) {
			quiz.getNextQuestion(1);
			answerStr = quiz.getCorrectAnswer();
			answer = Integer.parseInt(answerStr);
			result = quiz.isCorrect(answer);
			assertEquals(Quiz.CORRECT, result);
			assertEquals(0, mqListener.onWrongAnswerCt);
			assertEquals(i, mqListener.onRightAnswerCt);
			if (i < 3) {
				assertEquals(0, mqListener.onThreeRightFirstTimeCt);
			} else {
				assertEquals(1, mqListener.onThreeRightFirstTimeCt);
			}
		}
	}

	@Override
	public void onLogMessage(String message) {
		System.out.println(message);
	}

}
