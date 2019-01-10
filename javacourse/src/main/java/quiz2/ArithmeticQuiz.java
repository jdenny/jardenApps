package quiz2;

import java.util.Random;

public class ArithmeticQuiz {
	private Random randomNum = new Random();
	private String question;
	private int correctAnswer;
	
	public ArithmeticQuiz() {
		
	}
	public String getNextQuestion() {
		int a = randomNum.nextInt(20) + 1;
		int b = randomNum.nextInt(20) + 1;
		int opCode = randomNum.nextInt(4);
		char op;
		switch(opCode) {
		case 0:
			op = '+';
			correctAnswer = a + b;
			break;
		case 1:
			op = '-';
			correctAnswer = a - b;
			break;
		case 2:
			op = '*';
			correctAnswer = a * b;
			break;
		default:
			op = '/';
			// shuffle the values a bit, so that the answer is an integer
			correctAnswer = a;
			a = a * b;
			break;
		}
		question = a + " " + op + " " + b + " = ";
		return question;
	}
	public String getCurrentQuestion() {
		return question;
	}
	public boolean isCorrect(int answer) {
		return answer == correctAnswer;
	}
	public String getAnswer() {
		return Integer.toString(correctAnswer);
	}
}


