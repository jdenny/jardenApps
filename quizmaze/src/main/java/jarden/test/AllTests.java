package jarden.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ MathsCacheTest.class, MathsQuizTest.class, QuizCacheTest.class,
	QuizCallbackTest.class })
public class AllTests {

}
