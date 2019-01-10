package demo;

public class InsufficientFundsException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InsufficientFundsException() {
	}
	public InsufficientFundsException(String message) {
		super(message);
	}
	public InsufficientFundsException(String message, Throwable throwable) {
		super(message, throwable);
	}
	public InsufficientFundsException(Throwable throwable) {
		super(throwable);
	}
}
