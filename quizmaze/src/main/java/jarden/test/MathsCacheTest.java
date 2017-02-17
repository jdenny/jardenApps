package jarden.test;

import static org.junit.Assert.*;
import jarden.quiz.AlgebraQuiz;
import jarden.quiz.AmazeQuizCache;
import jarden.quiz.ArithmeticQuiz;
import jarden.quiz.FractionsQuiz;
import jarden.quiz.PowersQuiz;
import jarden.quiz.Quiz;
import jarden.quiz.QuizCache;
import jarden.quiz.QuizCacheListener;
import jarden.quiz.SeriesQuiz;
import jarden.quiz.TimesQuiz;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MathsCacheTest implements QuizCacheListener {
	private QuizCache quizCache;
	private Class<? extends Quiz> clazz = ArithmeticQuiz.class;
	private String quizType;
	
	@Parameters
	public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ ArithmeticQuiz.class, "Arithmetic" },
        		{ AlgebraQuiz.class, "Algebra" },
        		{ FractionsQuiz.class, "Fractions" },
        		{ PowersQuiz.class, "Powers" },
        		{ SeriesQuiz.class, "Series" },
        		{ TimesQuiz.class, "Times" }
        });
	}
	
	public MathsCacheTest(Class<? extends Quiz> clazz, String quizType) {
		this.clazz = clazz;
		this.quizType = quizType;
	}

	@Before
	public void setUp() throws Exception {
		quizCache = new AmazeQuizCache(this);
	}

	@After
	public void tearDown() throws Exception {
		quizCache = null;
	}
	
	@Test
	public void shouldGetMaths() throws IOException {
		Quiz quiz = quizCache.getQuiz("Maths", quizType);
		assertTrue(clazz.isInstance(quiz));
		Quiz quiz2 = quizCache.getQuiz("Maths", quizType);
		assertTrue(clazz.isInstance(quiz2));
		assertSame(quiz, quiz2); // this is what a cache does!
	}

	@Override
	public void onLogMessage(String message) {
		System.out.println(message);
	}
}
