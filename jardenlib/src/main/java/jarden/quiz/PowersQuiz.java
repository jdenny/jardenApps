package jarden.quiz;

public class PowersQuiz extends Quiz {

	public PowersQuiz() {
	}
	@Override
	public String getNextQuestion(int level) {
		int maxInt = super.getMaxInt(level, 5);
		int a = randomNum.nextInt(maxInt);
		int opCode = randomNum.nextInt(4);
		int correctAnswer;
		String question;

		if (opCode == 0) { // square
			correctAnswer = a * a;
			question = "what is " + a + " squared?";
		}
		else if (opCode == 1) { // cube
			correctAnswer = a * a * a;
			question = "what is " + a + " cubed?";
		}
		else if (opCode == 2) { // square root
			correctAnswer = a;
			question = "what is the square root of " + a * a +"?";
		}
		else { // opCode must be 3: cube root
			correctAnswer = a;
			question = "what is the cube root of " + a * a * a +"?";
		}
		super.setQuestionAnswer(question, correctAnswer);
		return question;
	}
	@Override
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_INT;
	}
	@Override
	public int isCorrect(String answer) {
		int answerInt = Integer.parseInt(answer);
		return super.isCorrect(answerInt);
	}
}
