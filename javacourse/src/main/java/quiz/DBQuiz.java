package quiz;

/**
 * Quiz built using questions and answers held on a database.
 * The hard work is done in the abstract superclass TextQAQuiz
 * and the static method questionsFromDB.
 * 
 * @author John
 */
public class DBQuiz extends TextQAQuiz {
	private final static String questionTemplate = "what is the Spanish for: {0}?";
	
	public DBQuiz() {
		super(QuestionsFromDB.questionsFromDB("food"), questionTemplate);
	}
	public DBQuiz(String category) {
		super(QuestionsFromDB.questionsFromDB(category), questionTemplate);
	}
}
