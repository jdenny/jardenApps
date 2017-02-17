package jarden.quiz;

//import java.io.InputStream;
//import java.util.Properties;

/**
 * Quiz built using questions and answers held on a database.
 * The hard work is done in the abstract superclass PresetQuiz
 * and the static method questionsFromDB.
 * 
 * @author John
 */
public class DBQuiz extends PresetQuiz {
	private final static String questionTemplate = "what is the Spanish for: {0}?";
	
	public DBQuiz() {
		super(QuestionsFromDB.questionsFromDB("food"), questionTemplate);
	}
//	public DBQuiz(Properties connectionProps, String category,
//			String questionTemplate) {
//		super(QuestionsFromDB.questionsFromDB(
//				conectionProps, category), questionTemplate);
//	}
}
