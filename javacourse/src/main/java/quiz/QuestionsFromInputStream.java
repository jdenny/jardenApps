package quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;


public class QuestionsFromInputStream {

	public static ArrayList<QuestionAnswer> questionsFromProperties(InputStream is) {
		try {
			Properties qaProps = new Properties();
			qaProps.load(is);
			Set<String> names = qaProps.stringPropertyNames();
			ArrayList<QuestionAnswer> qaList = new ArrayList<QuestionAnswer>();
			for (String name: names) {
				qaList.add(new QuestionAnswer(name, qaProps.getProperty(name)));
			}
			return qaList;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static ArrayList<QuestionAnswer> questionsFromInputStream(InputStream is) {
		try {
			InputStreamReader isReader = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isReader);
			ArrayList<QuestionAnswer> qaList = new ArrayList<QuestionAnswer>();
			String question = null;
			String answer;
			while (true) {
				String line = reader.readLine();
				if (line == null) break; // end of file
				if (line.startsWith("#")) continue;
				if (line.startsWith("Q: ")) {
					question = line.substring(3);
				} else if (line.startsWith("A: ")) {
					answer = line.substring(3);
					qaList.add(new QuestionAnswer(question, answer));
				} else {
					System.out.println("unrecognised line: " + line);
				}
			}
			reader.close();
			return qaList;
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
