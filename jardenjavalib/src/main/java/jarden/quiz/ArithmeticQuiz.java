package jarden.quiz;

public class ArithmeticQuiz extends Quiz {

	public ArithmeticQuiz() {
	}
	@Override
	public String getNextQuestion(int level) {
		int maxInt = super.getMaxInt(level);
		final int opCode = randomNum.nextInt(4);
		// weight the maxInt according to the operator,
		// so the result is a similar magnitude:
		switch (opCode) {
		case 0: // add
			maxInt = (maxInt + 1) / 2;
			break;
		case 1: // subtract
			break;
		case 2: // multiply
		case 3: // divide 
			maxInt = (int)Math.sqrt(maxInt) + 1;
			break;
		}
		int a = randomNum.nextInt(maxInt) + 1;
		int b = randomNum.nextInt(maxInt) + 1;
		char op;
		int correctAnswer;
		switch(opCode) {
		case 0:
			op = '+';
			correctAnswer = a + b;
			break;
		case 1:
			op = '-';
			if (b > a) {
				// reverse, so answer is not negative:
				int temp = a;
				a = b;
				b = temp;
			}
			correctAnswer = a - b;
			break;
		case 2:
			op = '*';
			correctAnswer = a * b;
			break;
		default:
			// assert opCode == 3: "unexpected opCode=" + opCode;
			op = '/';
			// shuffle the values a bit, so that the answer is an integer
			correctAnswer = a;
			a = a * b;
			break;
		}
		String question = a + " " + op + " " + b + " ?";
		super.setQuestionAnswer(question, correctAnswer);
		return question;
	}
	@Override
	public int isCorrect(String answer) {
		int answerInt = Integer.parseInt(answer);
		return super.isCorrect(answerInt);
	}

	@Override
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_INT;
	}
}


