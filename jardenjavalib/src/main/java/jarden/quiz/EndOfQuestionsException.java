package jarden.quiz;

public class EndOfQuestionsException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public EndOfQuestionsException() {}
	public EndOfQuestionsException(String message) {
		super(message);
	}
}
