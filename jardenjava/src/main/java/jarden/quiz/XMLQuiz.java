package jarden.quiz;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * First draft version of XMLQuiz, which can be written to an XML file, or
 * created from an XML file. Not sure if this has a role in the great Quiz
 * project, but here it is anyway!
 * @author John
 */
public class XMLQuiz implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<QuestionAnswer> qaList;
	private String questionTemplate;

	public static void main(String[] args) throws IOException {
		ArrayList<QuestionAnswer> qaList = new ArrayList<QuestionAnswer>();
		qaList.add(new QuestionAnswer("how are you?", "comment allez vous"));
		qaList.add(new QuestionAnswer("hello?", "bonjour"));
		qaList.add(new QuestionAnswer("my name is", "mon nombre est"));
		XMLQuiz xmlQuiz = new XMLQuiz(qaList, "please translate");
		File file = new File("/temp/quiz.xml");
		writeQuizToXMLFile(xmlQuiz, file);
		xmlQuiz = XMLQuiz.readQuizFromXMLFile(file);
		for (QuestionAnswer qa: xmlQuiz.getQaList()) {
			System.out.println(qa.getQuestion() + ": " + qa.getAnswer());
		}
		
	}
	public ArrayList<QuestionAnswer> getQaList() {
		return qaList;
	}
	public void setQaList(ArrayList<QuestionAnswer> qaList) {
		this.qaList = qaList;
	}
	public XMLQuiz() {
	}
	public XMLQuiz(ArrayList<QuestionAnswer> qaList, String questionTemplate) {
		this.qaList = qaList;
		this.questionTemplate = questionTemplate;
	}
	public String getQuestionTemplate() {
		return questionTemplate;
	}
	public void setQuestionTemplate(String questionTemplate) {
		this.questionTemplate = questionTemplate;
	}
	/**
	 * For each Quiz object, write a serialised version of the object
	 * to an XML file.
	 */
	public static void writeQuizToXMLFile(XMLQuiz quiz, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		XMLEncoder xmlEncoder = new XMLEncoder(bos);
		xmlEncoder.writeObject(quiz);
		xmlEncoder.close();
	}
	/**
	 * Read Quiz objects from the XML file, which it assumes has been
	 * created with XMLEncoder, and add to an ArrayList
	 * which is returned when EOF is reached.
	 */
	public static XMLQuiz readQuizFromXMLFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		XMLDecoder xmlDecoder = new XMLDecoder(bis);
		XMLQuiz quiz = (XMLQuiz)xmlDecoder.readObject();
		xmlDecoder.close();
		return quiz;
	}
}
