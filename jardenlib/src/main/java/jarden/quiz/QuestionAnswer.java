package jarden.quiz;

public class QuestionAnswer {
	public String question;
	public String answer;
	public String helpText;

	public QuestionAnswer() {
	}
	public QuestionAnswer(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}
    public QuestionAnswer(String question, String answer, String helpText) {
	    this(question, answer);
        this.helpText = helpText;
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

