package quiz3;

public class ArithmeticQuiz extends Quiz {

	public ArithmeticQuiz() {
	}
	@Override
	public String getNextQuestion() {
		int a = randomNum.nextInt(20) + 1;
		int b = randomNum.nextInt(20) + 1;
		int opCode = randomNum.nextInt(4);
		char op;
		int correctAnswer;
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
		String question = a + " " + op + " " + b + " = ";
		super.setQuestionAnswer(question, correctAnswer);
		return question;
	}
	@Override
	public boolean isCorrect(String answer) {
		int answerInt = Integer.parseInt(answer);
		return super.isCorrect(answerInt);
	}
}


