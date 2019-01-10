package quiz;

import java.io.InputStream;
import java.nio.file.Paths;

/**
 * Quiz built using questions and answers held on a file.
 * Seemingly ridiculously easy implementation, but the work is done
 * in the abstract superclass TextQAQuiz and the static method
 * questionsFromFile.
 * 
 * @author John
 */
public class FileQuiz extends TextQAQuiz {
	private final static String questionTemplate = "what is the Spanish for: {0}?";
	
	public FileQuiz() {
		super(QuestionsFromFile.questionsFromFile(Paths.get("docs/tiempoQA.txt")),
				questionTemplate);
	}
	public FileQuiz(InputStream is, String questionTemplate) {
		super(QuestionsFromInputStream.questionsFromInputStream(
				is), questionTemplate);
	}
}
