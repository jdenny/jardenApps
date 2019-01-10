package quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

public class QuestionsFromFile {

	public static void main(String[] args) {
		Path path = Paths.get("docs/spanishQA.txt");
		ArrayList<QuestionAnswer> qaList = questionsFromFile(path);
		System.out.println("Question/Answer list from " + path.toAbsolutePath());
		for (QuestionAnswer qa: qaList) {
			System.out.println("  " + qa);
		}
		
		path = Paths.get("docs/spanishQA.properties");
		qaList = questionsFromProperties(path);
		System.out.println("Question/Answer list from " + path.toAbsolutePath());
		for (QuestionAnswer qa: qaList) {
			System.out.println("  " + qa);
		}
	}
	public static ArrayList<QuestionAnswer> questionsFromProperties(Path path) {
		try (Reader fReader = Files.newBufferedReader(path, Charset.defaultCharset())) {
			Properties qaProps = new Properties();
			qaProps.load(fReader);
			Set<String> names = qaProps.stringPropertyNames();
			ArrayList<QuestionAnswer> qaList = new ArrayList<>();
			for (String name: names) {
				qaList.add(new QuestionAnswer(name, qaProps.getProperty(name)));
			}
			return qaList;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static ArrayList<QuestionAnswer> questionsFromFile(Path path) {
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset())) {
			ArrayList<QuestionAnswer> qaList = new ArrayList<>();
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
			return qaList;
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
