package quiz;

public class QuestionAnswer {
	public String question;
	public String answer;
	public QuestionAnswer(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}
	public String toString() {
		return question + ": " + answer;
	}
}

