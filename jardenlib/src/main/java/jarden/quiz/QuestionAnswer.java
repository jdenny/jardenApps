package jarden.quiz;

public class QuestionAnswer {
	public String question;
	public String answer;

	public QuestionAnswer() {
	}
	public QuestionAnswer(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String toString() {
		return question + ": " + answer;
	}
}

