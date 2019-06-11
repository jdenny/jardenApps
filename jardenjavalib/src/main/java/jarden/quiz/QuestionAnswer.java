package jarden.quiz;

import jarden.cards.ParsedAnswer;

public class QuestionAnswer {
	public String question;
	public String answer;
	public String notes;
    private ParsedAnswer parsedAnswer;

	public QuestionAnswer() {
	}
	public QuestionAnswer(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}
    public QuestionAnswer(String question, String answer, String notes) {
	    this(question, answer);
        this.notes = notes;
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
    public ParsedAnswer getParsedAnswer() throws NumberFormatException {
        if (parsedAnswer == null) {
            parsedAnswer = new ParsedAnswer(answer);
        }
        return parsedAnswer;
    }
}

